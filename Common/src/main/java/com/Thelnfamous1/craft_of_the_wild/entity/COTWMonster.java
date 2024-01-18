package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.init.DamageTypeInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.core.particles.ParticleTypes;
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

public abstract class COTWMonster<T extends AnimatedAttacker.AttackType> extends Monster implements GeoEntity, AnimatedAttacker<T> {
    public static final EntityDataAccessor<Integer> DATA_LAST_ATTACK_TICK = SynchedEntityData.defineId(COTWMonster.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected int customDeathTime;

    public COTWMonster(EntityType<? extends Monster> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LAST_ATTACK_TICK, 0);
    }

    protected void executeAttack(T currentAttackType){
        this.playAttackSound(currentAttackType);
        switch (currentAttackType.getDamageMode()){
            case AREA_OF_EFFECT -> {
                AABB attackBox = this.createAttackBox();
                if(!this.level().isClientSide){
                    List<LivingEntity> targets = this.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, attackBox);
                    targets.forEach(target -> this.doHurtAreaOfEffectTarget(target, currentAttackType.getBaseDamageModifier()));
                }
                this.finalizeAreaOfEffectAttack(attackBox);
            }
            case MELEE -> {
                LivingEntity target = this.getTarget();
                if (target != null && this.isWithinMeleeAttackRange(target)) {
                    super.doHurtTarget(target);
                }
            }
        }
    }

    protected void finalizeAreaOfEffectAttack(AABB attackBox) {
        if(!this.level().isClientSide){
            Vec3 center = attackBox.getCenter();
            double radius = attackBox.getSize() * 0.5;
            Vec3 particlePos = center.subtract(0, attackBox.getYsize() * 0.5D + 0.5D, 0);
            if(radius > 2){
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, particlePos.x, particlePos.y, particlePos.z, 0, 1.0D, 0.0D, 0.0D, 1);
            } else{
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION, particlePos.x, particlePos.y, particlePos.z, 0, 1.0D, 0.0D, 0.0D, 1);
            }
        }
    }

    protected AABB createAttackBox() {
        double attackRadius = this.getAttackRadius();
        Vec3 baseOffset = new Vec3(0.0D, 0.0D, this.getBbWidth() * 0.5F).yRot(-this.getYHeadRot() * Mth.DEG_TO_RAD);
        Vec3 attackOffset = new Vec3(0.0D, 0.0D, attackRadius * this.getScale()).yRot(-this.getYHeadRot() * Mth.DEG_TO_RAD);
        double attackSize = attackRadius * 2;
        return AABB.ofSize(this.getBoundingBox().getCenter().add(baseOffset).add(attackOffset), attackSize, attackSize, attackSize);
    }

    protected abstract double getAttackRadius();

    // Largely the same as Mob#doHurtTarget, but with adjustments for area of effect attacks and damage scaling
    protected void doHurtAreaOfEffectTarget(Entity target, double baseDamageModifier){
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        attackDamage *= baseDamageModifier;
        float attackKnockback = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            attackDamage += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) target).getMobType());
            attackKnockback += (float)EnchantmentHelper.getKnockbackBonus(this);
        }

        int fireAspect = EnchantmentHelper.getFireAspect(this);
        if (fireAspect > 0) {
            target.setSecondsOnFire(fireAspect * 4);
        }

        boolean hurt = target.hurt(DamageTypeInit.mobAreaOfEffectAttack(this),
                attackDamage);
        if (hurt) {
            if (attackKnockback > 0.0F && target instanceof LivingEntity) {
                ((LivingEntity) target).knockback(attackKnockback * 0.5F, Mth.sin(this.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(this.getYRot() * Mth.DEG_TO_RAD));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            this.doEnchantDamageEffects(this, target);
            this.setLastHurtMob(target);
        }

    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity target) {
        return Mth.square(COTWUtil.getHitboxAdjustedDistance(this, target, this.getAttackRadius()));
    }

    @Override
    public int getTicksSinceLastAttack() {
        return this.tickCount - this.getLastAttackTick();
    }

    public int getLastAttackTick() {
        return this.entityData.get(DATA_LAST_ATTACK_TICK);
    }

    public void setLastAttackTick(int lastAttackTick) {
        this.entityData.set(DATA_LAST_ATTACK_TICK, lastAttackTick);
    }

    protected abstract void playAttackSound(T currentAttackType);

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

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("COTWDeathTime", (short)this.customDeathTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("COTWDeathTime", Tag.TAG_ANY_NUMERIC)){
            this.customDeathTime = tag.getShort("COTWDeathTime");
        }
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
        if(!this.level().isClientSide && !this.isAttackAnimationInProgress()){
            this.setLastAttackTick(this.tickCount);
            if(this.getCurrentAttackType() == null){
                this.setCurrentAttackType(this.selectAttackType(target));
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getCurrentAttackType() != null){
            if(this.isAttackAnimationInProgress()){
                if(this.isTimeToAttack()){
                    this.executeAttack(this.getCurrentAttackType());
                }
            } else if(!this.level().isClientSide){
                this.setCurrentAttackType(null);
            }
        }
    }

    protected abstract T selectAttackType(Entity target);

    protected abstract int getMaxDeathTime();
}
