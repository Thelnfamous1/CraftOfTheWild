package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.controller.AttackingLookController;
import com.Thelnfamous1.craft_of_the_wild.init.DamageTypeInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Supplier;

public abstract class COTWMonster<T extends AnimatedAttacker.AttackType> extends Monster implements GeoEntity, AnimatedAttacker<T> {
    public static final EntityDataAccessor<Boolean> DATA_ATTACKING = SynchedEntityData.defineId(COTWMonster.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_WALKING = SynchedEntityData.defineId(COTWMonster.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected int customDeathTime;
    protected int attackTicker;
    protected int attackCooldownTicks;

    public COTWMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.lookControl = new AttackingLookController<>(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACKING, false);
        this.entityData.define(DATA_WALKING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if(dataAccessor.equals(DATA_ATTACKING)){
            this.attackTicker = 0;
        }
    }

    @Override
    public boolean canRotateDuringAttack(T currentAttackType) {
        return false;
    }

    protected void executeAttack(T currentAttackType, AttackPoint currentAttackPoint){
        this.playAttackSound(currentAttackType, currentAttackPoint);
        switch (currentAttackPoint.damageMode()){
            case AREA_OF_EFFECT -> {
                AABB attackBox = this.createAttackBox(currentAttackType);
                COTWCommon.debug(Constants.DEBUG_MONSTER, "Created attack box of size {} for {}", attackBox.getSize(), this);
                if(Constants.DEBUG_MONSTER) COTWUtil.sendHitboxParticles(attackBox, this.level());
                if(!this.level().isClientSide){
                    List<LivingEntity> targets = this.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, attackBox);
                    targets.forEach(target -> this.modifiedDoHurtTarget(target, currentAttackPoint.baseDamageModifier(), true));
                }
                this.finalizeAreaOfEffectAttack(attackBox);
            }
            case MELEE -> {
                LivingEntity target = this.getTarget();
                if (target != null && this.isWithinMeleeAttackRange(target, 1)) {
                    this.modifiedDoHurtTarget(target, currentAttackPoint.baseDamageModifier(), false);
                }
            }
            case RANGED -> {
                LivingEntity target = this.getTarget();
                if (target != null) {
                    this.doRangedAttack(currentAttackType, currentAttackPoint, target);
                } else{
                    // shoot at a target position that is a predetermined distance away from this mob's eye position
                    Vec3 baseOffset = COTWUtil.yRotatedZVector(this.getBbWidth() * 0.5F, this.getYHeadRot());
                    Vec3 shootVec = this.getViewVector(1.0F).normalize().scale(this.getProjectileMinimumShootRange());
                    Vec3 targetVec = this.getEyePosition().add(baseOffset).add(shootVec);
                    double targetX = targetVec.x;
                    double targetY = targetVec.y;
                    double targetZ = targetVec.z;
                    this.doRangedAttack(currentAttackType, currentAttackPoint, targetX, targetY, targetZ);
                }
            }
            case CUSTOM -> {
                this.doCustomAttack(currentAttackType, currentAttackPoint);
            }
        }
    }

    protected void doCustomAttack(T currentAttackType, AttackPoint currentAttackPoint) {

    }

    protected void doRangedAttack(T currentAttackType, AttackPoint currentAttackPoint, LivingEntity target){
        this.doRangedAttack(currentAttackType, currentAttackPoint, target.getX(), target.getY() + (double)target.getEyeHeight() * 0.5D, target.getZ());
    }

    protected void doRangedAttack(T currentAttackType, AttackPoint currentAttackPoint, double targetX, double targetY, double targetZ){
    }

    protected double getProjectileMinimumShootRange() {
        return 15;
    }

    protected void finalizeAreaOfEffectAttack(AABB attackBox) {
        if(!this.level().isClientSide){
            Vec3 center = attackBox.getCenter();
            double radius = attackBox.getSize() * 0.5;
            Vec3 particlePos = center.subtract(0, (attackBox.getYsize() * 0.5D) - 0.5D, 0);
            COTWUtil.spawnVanillaExplosionParticles(((ServerLevel) this.level()), radius, particlePos);
        }
    }

    protected AABB createAttackBox(T currentAttackType) {
        double attackRadius = this.getAttackRadius(currentAttackType);
        Vec3 baseOffset = COTWUtil.yRotatedZVector(this.getBbWidth() * 0.5F, this.getYHeadRot());
        Vec3 attackOffset = COTWUtil.yRotatedZVector(attackRadius, this.getYHeadRot());
        double attackSize = attackRadius * 2;
        return AABB.ofSize(this.position().add(0, attackRadius, 0).add(baseOffset).add(attackOffset), attackSize, attackSize, attackSize);
    }

    protected abstract double getAttackRadius(T currentAttackType);

    // Largely the same as Mob#doHurtTarget, but with adjustments for area of effect attacks and damage scaling
    protected void modifiedDoHurtTarget(Entity target, double baseDamageModifier, boolean isAreaOfEffect){
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        attackDamage *= baseDamageModifier;
        float attackKnockback = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity livingTarget) {
            attackDamage += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), livingTarget.getMobType());
            attackKnockback += (float)EnchantmentHelper.getKnockbackBonus(this);
        }

        int fireAspect = EnchantmentHelper.getFireAspect(this);
        if (fireAspect > 0) {
            target.setSecondsOnFire(fireAspect * 4);
        }

        boolean hurt = target.hurt(isAreaOfEffect ? DamageTypeInit.mobAreaOfEffectAttack(this) : this.level().damageSources().mobAttack(this),
                attackDamage);
        if (hurt) {
            if (attackKnockback > 0.0F && target instanceof LivingEntity livingTarget) {
                livingTarget.knockback(attackKnockback * 0.5F, Mth.sin(this.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(this.getYRot() * Mth.DEG_TO_RAD));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            this.doEnchantDamageEffects(this, target);
            this.setLastHurtMob(target);
        }
    }

    protected boolean vanillaDoHurtTarget(Entity target){
        return super.doHurtTarget(target);
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) {
        return this.isWithinMeleeAttackRange(target, 1);
    }

    protected boolean isWithinMeleeAttackRange(LivingEntity target, double radiusScale) {
        double distanceSqr = this.getPerceivedTargetDistanceSquareForMeleeAttack(target);
        return distanceSqr <= this.getMeleeAttackRangeSqr(target, radiusScale);
    }

    @Override
    public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity target) {
        return COTWUtil.getDistSqrBetweenHitboxes(this, target);
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity target) {
        return this.getMeleeAttackRangeSqr(target, 1);
    }

    protected double getMeleeAttackRangeSqr(LivingEntity target, double radiusScale){
        T currentAttackType = this.getCurrentAttackType();
        if(currentAttackType != null){
            return Mth.square(this.getAttackRadius(currentAttackType) * radiusScale);
        } else{
            return super.getMeleeAttackRangeSqr(target);
        }
    }

    @Override
    public int getTicksSinceAttackStarted() {
        return this.attackTicker;
    }

    @Override
    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    public void setAttacking(boolean attacking, boolean force) {
        this.entityData.set(DATA_ATTACKING, attacking, force);
    }

    protected abstract void playAttackSound(T currentAttackType, AttackPoint currentAttackPoint);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void tickDeath() {
        ++this.customDeathTime;
        if (this.customDeathTime >= this.getMaxDeathTime() && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    public boolean isWalking(){
        return this.entityData.get(DATA_WALKING);
    }

    protected void setIsWalking(boolean isWalking){
        this.entityData.set(DATA_WALKING, isWalking);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(!this.level().isClientSide){
            this.setIsWalking(this.zza > 0);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("COTWDeathTime", Tag.TAG_ANY_NUMERIC)){
            this.customDeathTime = tag.getShort("COTWDeathTime");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("COTWDeathTime", (short)this.customDeathTime);
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        if (super.isAlliedTo(other)) {
            return true;
        } else if (this.isAlliedToDefault(other)) {
            return this.getTeam() == null && other.getTeam() == null;
        } else {
            return false;
        }
    }

    protected boolean isAlliedToDefault(Entity other) {
        return other.getType().equals(this.getType());
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return this.startAttacking(target);
    }

    protected boolean startAttacking(Entity target) {
        return this.startAttack(() -> this.selectAttackTypeForTarget(target), false);
    }

    protected boolean startAttack(Supplier<T> attackTypeSelector, boolean force) {
        if(!this.level().isClientSide && (force || !this.isAttackAnimationInProgress() && !this.isAttackCoolingDown())){
            this.setAttacking(true, force);
            if(force || this.getCurrentAttackType() == null) {
                this.setCurrentAttackType(attackTypeSelector.get(), force);
            }
            this.startAttackCooldown();
            this.onAttackStarted(this.getCurrentAttackType());
            return true;
        }
        return false;
    }

    protected void onAttackStarted(T currentAttackType) {

    }

    protected void startAttackCooldown() {
        this.attackCooldownTicks = AnimatedAttacker.optionalCurrentAttackType(this)
                .map(AttackType::getAttackDuration)
                .orElse(0) + 20;
    }

    protected boolean isAttackCoolingDown() {
        return this.attackCooldownTicks > 0;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.attackCooldownTicks > 0){
            this.attackCooldownTicks--;
        }
        T currentAttackType = this.getCurrentAttackType();
        if(currentAttackType != null && this.isAttacking()){
            if(this.isAttackAnimationInProgress()){
                AttackPoint currentAttackPoint = this.getCurrentAttackPoint();
                if(currentAttackPoint != null){
                    this.executeAttack(currentAttackType, currentAttackPoint);
                }
                this.attackTicker++;
            } else if(!this.level().isClientSide){
                this.setCurrentAttackType(null, false);
                this.setAttacking(false, false);
            }
        }
    }

    protected void updateCurrentAttackTypeForTarget() {
        LivingEntity target = this.getTarget();
        if(target != null && !this.level().isClientSide){
            T currentAttackType = this.getCurrentAttackType();
            if(currentAttackType == null){
                this.setCurrentAttackType(this.selectAttackTypeForTarget(target), false);
            } else if(!this.isAttackAnimationInProgress() && !this.isAttackCoolingDown()){
                this.adjustCurrentAttackTypeForTarget(currentAttackType, target);
            }
        }
    }

    protected void adjustCurrentAttackTypeForTarget(T currentAttackType, LivingEntity target) {
    }

    protected abstract T selectAttackTypeForTarget(Entity target);

    protected abstract int getMaxDeathTime();
}
