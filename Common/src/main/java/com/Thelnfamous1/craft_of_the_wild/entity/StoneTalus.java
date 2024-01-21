package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.*;
import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.init.AttributeInit;
import com.Thelnfamous1.craft_of_the_wild.init.MemoryModuleInit;
import com.Thelnfamous1.craft_of_the_wild.mixin.EntityAccessor;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.*;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public class StoneTalus extends COTWMonster<StoneTalusAttackType> implements SmartBrainOwner<StoneTalus>, MultipartEntity, StoneTalusBase, RangedAttackMob {
    public static final float SCALE = 1F; // desired target is 7/3
    protected static final EntityDataAccessor<OptionalInt> DATA_ATTACK_TYPE_ID = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    protected static final EntityDataAccessor<Integer> DATA_LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_BATTLE = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.BOOLEAN);
    public static final int EMERGE_TICKS = COTWUtil.secondsToTicks(2.8333F);
    public static final int DIG_TICKS = COTWUtil.secondsToTicks(6.2083F);
    public static final int DEATH_TICKS = COTWUtil.secondsToTicks(2.5F);
    public static final int WITHER_SHOOT_EVENT_ID = 1024;
    private final Entity[] partEntities;
    private final PartEntityController<? extends Entity> partEntityController;
    private final ServerBossEvent bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
    @Nullable
    private StoneTalusAttackType currentAttackType;

    public StoneTalus(EntityType<? extends StoneTalus> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
        this.xpReward = 60;
        this.partEntityController = Services.PLATFORM.makePartEntityController(this,
                new PartEntityController.Info("weakPoint", 0.6875F, 0.6875F, true, 0, 2.9375, 0, StoneTalus.SCALE),
                new PartEntityController.Info("head", 3.125F, 1.125F, true, 0, 1.8125, 0, StoneTalus.SCALE),
                new PartEntityController.Info("body", 2.625F, 1.0625F, true, 0, 0.75, 0, StoneTalus.SCALE),
                new PartEntityController.Info("leftArm", 1.125F, 1.5625F, true, -1.6875, 0, 0, StoneTalus.SCALE),
                new PartEntityController.Info("rightArm", 1.125F, 1.5625F, true, 1.6875, 0, 0, StoneTalus.SCALE),
                new PartEntityController.Info("leftLeg", 0.5625F, 0.75F, true, -0.5, 0, 0, StoneTalus.SCALE),
                new PartEntityController.Info("rightLeg", 0.5625F, 0.75F, true, 0.5, 0, 0, StoneTalus.SCALE));
        this.partEntities = this.partEntityController.getParts().toArray(Entity[]::new);
        // Forge: Fix MC-158205: Make sure part ids are successors of parent mob id
        this.setId(EntityAccessor.craft_of_the_wild$getENTITY_COUNTER().getAndAdd(this.partEntities.length + 1) + 1);
    }

    public static boolean canDestroy(BlockState blockState) {
        return blockState.is(COTWTags.STONE_TALUS_CAN_DESTROY);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(AttributeInit.PROJECTILE_RESISTANCE.get(), 0.7D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public Entity[] getPartEntities() {
        return this.partEntities;
    }

    @Override
    public void setId(int pId) {
        super.setId(pId);
        // Forge: Fix MC-158205: Set part ids to successors of parent mob id
        for (int idx = 0; idx < this.partEntities.length; idx++)
            this.partEntities[idx].setId(pId + idx + 1);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new StoneTalusBodyRotationControl<>(this);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Pose.SLEEPING) ? 1 : 0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        if (pPacket.getData() == 1) {
            this.setPose(Pose.SLEEPING);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isRemoved();
    }

    @Override
    public boolean isPushable() {
        return !this.isInsideGround() && super.isPushable();
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return super.checkSpawnObstruction(pLevel) && pLevel.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return this.isInsideGround() && !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || super.isInvulnerableTo(pSource);
    }

    public boolean isInsideGround() {
        return this.hasPose(Pose.DIGGING) || this.hasPose(Pose.EMERGING) || this.hasPose(Pose.SLEEPING);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distToNearestPlayer) {
        return false;
    }

    @Override
    public boolean canDisableShield() {
        return AnimatedAttacker.hasCurrentAttackType(this, StoneTalusAttackType.PUNCH);
    }

    @Override
    public boolean ignoreExplosion() {
        return this.isInsideGround();
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        resetDigCooldown(this);
        if (pReason == MobSpawnType.NATURAL) {
            this.setPose(Pose.SLEEPING);
            BrainUtils.setMemory(this, MemoryModuleInit.IS_SLEEPING.get(), true);
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        EntityDimensions baseDimensions = super.getDimensions(Pose.STANDING);
        return this.isInsideGround() ? EntityDimensions.fixed(baseDimensions.width, 1.0F) : super.getDimensions(pPose);
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return 0.0F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACK_TYPE_ID, OptionalInt.empty());
        this.entityData.define(DATA_LAST_POSE_CHANGE_TICK, 0);
        this.entityData.define(DATA_BATTLE, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public boolean isBattle() {
        return this.entityData.get(DATA_BATTLE);
    }

    @Override
    public void setBattle(boolean battle){
        this.entityData.set(DATA_BATTLE, battle);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
        if(tag.contains("Sleeping", Tag.TAG_ANY_NUMERIC) && tag.getBoolean("Sleeping")){
            this.setPose(Pose.SLEEPING);
        }
        this.readBattleFromTag(tag);
        // Doing this here since SBL does not deserialize Brain NBT
        COTWUtil.readBrainFromTag(tag, this);
        COTWUtil.debugMemoryStatus(Constants.DEBUG_STONE_TALUS, this, MemoryModuleInit.IS_SLEEPING.get());
        COTWUtil.debugMemoryStatus(Constants.DEBUG_STONE_TALUS, this, MemoryModuleInit.DIG_COOLDOWN.get());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sleeping", this.hasPose(Pose.SLEEPING));
        this.writeBattleToTag(tag);
    }

    @Override
    protected int decreaseAirSupply(int pAir) {
        return pAir;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return false; // Talus is immune to potions
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.refuseToMove(true) && this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
            pTravelVector = pTravelVector.multiply(0.0D, 1.0D, 0.0D);
        }
        super.travel(pTravelVector);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.refuseToMove(true)) {
            this.clampHeadRotationToBody(this, this.getMaxHeadYRot());
        }
    }

    public void clientDiggingParticles(AnimationState<StoneTalus> state) {
        int animationTicks = (int) (state.getAnimationTick() - this.getLastPoseChangeTick());
        //Constants.LOG.info("Current animation ticks for {}: {}", this.getName().getString(), animationTicks);
        if(this.isVisuallyDigging(animationTicks) || this.isVisuallyEmerging(animationTicks)){
            RandomSource random = this.getRandom();
            BlockState blockStateOn = this.getBlockStateOn();
            if (blockStateOn.getRenderShape() != RenderShape.INVISIBLE) {
                float radius = this.getBbWidth() * 0.5F;
                for(int i = 0; i < 30; ++i) {
                    double x = this.getX() + (double)Mth.randomBetween(random, -radius, radius);
                    double y = this.getY();
                    double z = this.getZ() + (double)Mth.randomBetween(random, -radius, radius);
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockStateOn), x, y, z, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    private boolean isVisuallyEmerging(int animationTicks) {
        return this.hasPose(Pose.EMERGING)
                && animationTicks <= COTWUtil.secondsToTicks(2);
    }

    private boolean isVisuallyDigging(int animationTicks) {
        return this.hasPose(Pose.DIGGING)
                && animationTicks >= COTWUtil.secondsToTicks(0.83F)
                && animationTicks <= COTWUtil.secondsToTicks(1.42F);
    }

    protected void clampHeadRotationToBody(Entity entityToUpdate, float maxHeadYRot) {
        float yHeadRot = entityToUpdate.getYHeadRot();
        float diff = Mth.wrapDegrees(this.yBodyRot - yHeadRot);
        float clampedDiff = Mth.clamp(diff, -maxHeadYRot, maxHeadYRot);
        float clampedHeadRotation = yHeadRot + diff - clampedDiff;
        entityToUpdate.setYHeadRot(clampedHeadRotation);
    }

    @Nullable
    public LivingEntity getTarget() {
        return BrainUtils.getTargetOfEntity(this);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        BrainUtils.setTargetOfEntity(this, target);
    }

    @Override
    public @Nullable StoneTalusAttackType getCurrentAttackType() {
        if(!this.level().isClientSide){
            return this.currentAttackType;
        } else{
            OptionalInt id = this.entityData.get(DATA_ATTACK_TYPE_ID);
            return id.isPresent() ? StoneTalusAttackType.byId(id.getAsInt()) : null;
        }
    }

    @Override
    public void setCurrentAttackType(@Nullable StoneTalusAttackType attackType) {
        this.currentAttackType = attackType;
        this.entityData.set(DATA_ATTACK_TYPE_ID, attackType == null ? OptionalInt.empty() : OptionalInt.of(attackType.getId()));
    }

    @Override
    protected void playAttackSound(StoneTalusAttackType currentAttackType, AttackPoint currentAttackPoint) {
        if(!this.level().isClientSide){
            if(currentAttackPoint.damageMode() == AttackPoint.DamageMode.AREA_OF_EFFECT){
                COTWUtil.playVanillaExplosionSound(this);
            } else if(currentAttackType == StoneTalusAttackType.THROW){
                if (!this.isSilent()) {
                    this.level().levelEvent(null, WITHER_SHOOT_EVENT_ID, this.blockPosition(), 0);
                }
            } else{
                this.playSound(SoundEvents.IRON_GOLEM_ATTACK);
            }
        }
    }

    @Override
    protected double getAttackRadius(StoneTalusAttackType currentAttackType) {
        switch (currentAttackType){
            case HEADBUTT -> {
                return 1.5 * SCALE * this.getScale(); // diameter = 3, scaled up by 7/3 to be 7
            }
            case POUND, PUNCH -> {
                return 1 * SCALE * this.getScale(); // diameter = 2, scaled up by 7/3 to be 14/3 (4 + 2/3)
                //return 9D/14D * SCALE * this.getScale(); // diameter = 9/7, scaled up by 7/3 to be 3
            }
            default -> {
                return 0;
            }
        }
    }

    @Override
    protected StoneTalusAttackType selectAttackTypeForTarget(Entity target) {
        if(this.shouldUseRangedAttack(target)){
            return StoneTalusAttackType.THROW;
        }
        return this.selectMeleeAttackType();
    }

    private StoneTalusAttackType selectMeleeAttackType() {
        int nextInt = this.level().random.nextInt(9);
        if(nextInt == 0){ // 1 in 9 chance for headbutt
            return StoneTalusAttackType.HEADBUTT;
        } else if(nextInt < 5){ // 4 in 9 chance for pound
            return StoneTalusAttackType.POUND;
        } else{ // 4 in 9 chance for punch
            return StoneTalusAttackType.PUNCH;
        }
    }

    private boolean shouldUseRangedAttack(Entity target) {
        return COTWUtil.getDistSqrBetweenHitboxes(this, target) >= Mth.square(this.getProjectileMinimumShootRange());
    }

    @Override
    protected void adjustCurrentAttackTypeForTarget(StoneTalusAttackType currentAttackType, LivingEntity target) {
        boolean shouldUseRangedAttack = this.shouldUseRangedAttack(target);
        if(currentAttackType == StoneTalusAttackType.THROW && !shouldUseRangedAttack){
            this.selectMeleeAttackType();
        } else if(currentAttackType != StoneTalusAttackType.THROW && shouldUseRangedAttack){
            this.setCurrentAttackType(StoneTalusAttackType.THROW);
        }
    }

    @Override
    protected double getProjectileMinimumShootRange() {
        return 10;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float powerForTime) {
        this.startAttacking(target);
    }

    @Override
    public void doRangedAttack(StoneTalusAttackType currentAttackType, AttackPoint currentAttackPoint, double targetX, double targetY, double targetZ) {
        if(!this.level().isClientSide){
            int attackPointIndex = currentAttackType.getAttackPoints().indexOf(currentAttackPoint);
            // left arm, then right arm
            Vec3 armPosition = this.getEyePosition();
            switch (attackPointIndex){
                case 0 -> armPosition = armPosition.add(COTWUtil.yRotatedXZVector(-1, 1, this.getYHeadRot()).scale(SCALE));
                case 1 -> armPosition = armPosition.add(COTWUtil.yRotatedXZVector(1, 1, this.getYHeadRot()).scale(SCALE));
            }

            double x = armPosition.x;
            double y = armPosition.y;
            double z = armPosition.z;
            double xDist = targetX - x;
            double yDist = targetY - y;
            double zDist = targetZ - z;
            StoneTalusArm stoneTalusArm = new StoneTalusArm(this.level(), this, xDist, yDist, zDist);
            stoneTalusArm.setOwner(this);
            stoneTalusArm.setBaseDamage(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * currentAttackPoint.baseDamageModifier());

            stoneTalusArm.setPosRaw(x, y, z);
            this.level().addFreshEntity(stoneTalusArm);
        }
    }

    public int getLastPoseChangeTick() {
        return this.entityData.get(DATA_LAST_POSE_CHANGE_TICK);
    }

    @Override
    public void setPose(Pose pose) {
        super.setPose(pose);
        if(!this.level().isClientSide){
            this.setLastPoseChangeTick(this.tickCount);
        }
    }

    public void setLastPoseChangeTick(int lastPoseChangeTick) {
        this.entityData.set(DATA_LAST_POSE_CHANGE_TICK, lastPoseChangeTick);
    }

    public boolean refuseToMove(boolean checkAttacking){
        return this.isInsideGround() || this.isDeadOrDying() || checkAttacking && this.isAttackAnimationInProgress();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected void playStepSound(BlockPos stepPos, BlockState stepState) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    @Override
    protected int getMaxDeathTime() {
        return DEATH_TICKS;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.partEntityController.tick();
        if (this.horizontalCollision && Services.PLATFORM.canEntityGrief(this.level(), this)) {
            AABB searchBox = this.getBoundingBox().inflate(0.2D);
            COTWUtil.destroyBlocksInBoundingBox(searchBox, this.level(), this, StoneTalus::canDestroy);
        }
    }

    @Override
    protected void finalizeAreaOfEffectAttack(AABB attackBox) {
        super.finalizeAreaOfEffectAttack(attackBox);
        if(!this.level().isClientSide && Services.PLATFORM.canEntityGrief(this.level(), this)){
            COTWUtil.destroyBlocksInBoundingBox(attackBox, this.level(), this, StoneTalus::canDestroy);
        }
    }

    @Override
    public boolean isPickable() {
        return false; // Need to return false so the regular hitbox is not used for hit detection
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !this.level().isClientSide && this.partEntityController.getOptionalPart("body")
                .map(body -> this.hurt(body, source, amount))
                .orElseGet(() -> {
                    COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "Damaging {} with non-weakpoint damage {}, {}", this, source, amount);
                    return this.reallyHurt(source, amount);
                });
    }

    @Override
    public boolean hurt(Entity partEntity, DamageSource pSource, float pDamage) {
        if(partEntity == this.partEntityController.getPart("weakPoint")){
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "Hit weakpoint for {} with {}, {}", this, pSource, pDamage);
            return this.reallyHurt(pSource, pDamage);
        } else if(pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "Damaging {} with non-weakpoint damage {}, {}", this, pSource, pDamage);
            return this.reallyHurt(pSource, pDamage);
        }
        return false;
    }

    protected boolean reallyHurt(DamageSource pSource, float pAmount) {
        if(pSource.is(DamageTypeTags.IS_PROJECTILE)){
            pAmount -= pAmount * this.getAttributeValue(AttributeInit.PROJECTILE_RESISTANCE.get());
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(COTWAnimations.moveController(this));
        controllers.add(COTWAnimations.poseController(this));
        controllers.add(COTWAnimations.attackController(this));
    }

    @Override
    public void setCustomName(@Nullable Component customName) {
        super.setCustomName(customName);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossEvent.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossEvent.removePlayer(pPlayer);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(this.hasPose(Pose.SLEEPING) && this.tickCount % 20 == 0){
            this.heal(1.0F);
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.tickBrain(this);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this, true, false);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public void handleAdditionalBrainSetup(SmartBrain<? extends StoneTalus> brain) {
        // prevents the Talus from being in idle mode initially when it shouldn't be
        brain.setActiveActivityIfPossible(Activity.REST);
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(Activity.EMERGE, Activity.REST, Activity.DIG, Activity.FIGHT, Activity.IDLE);
    }

    @Override
    public List<? extends ExtendedSensor<? extends StoneTalus>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new SleepSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new AdditionalMemories<>(MemoryModuleInit.DIG_COOLDOWN.get()),
                COTWSharedAi.createVanillaStyleLookAtTarget(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new CustomBehaviour<>(StoneTalus::resetDigCooldown),
                new CustomBehaviour<>(StoneTalus::updateCurrentAttackTypeForTarget),
                new InvalidateAttackTarget<StoneTalus>().invalidateIf((talus, target) -> !talus.closerThan(target, COTWUtil.getHitboxAdjustedDistance(talus, target, COTWUtil.getFollowRange(talus)))),
                new FirstApplicableBehaviour<>(
                        new LookAtAttackTarget<StoneTalus>()
                                .startCondition(StoneTalus::isInRangedMode),
                        new COTWSetWalkTargetToAttackTarget<StoneTalus>()
                                .isWithinAttackRange((talus, target) -> talus.isWithinMeleeAttackRange(target, true))
                                .speedMod((talus, target) -> 1.0F)
                                .startCondition(StoneTalus::isInMeleeMode)
                ),
                new FirstApplicableBehaviour<>(
                        new COTWAnimatableRangedAttack<StoneTalus>(0)
                                .getPerceivedTargetDistanceSquared(COTWUtil::getDistSqrBetweenHitboxes)
                                .attackRadius(64)
                                .attackInterval(StoneTalus::getAttackCooldownDuration)
                                .startCondition(talus -> isInRangedMode(talus) && isLookingAtTarget(talus)),
                        new AnimatableMeleeAttack<StoneTalus>(0)
                                .attackInterval(StoneTalus::getAttackCooldownDuration)
                                .startCondition(talus -> isInMeleeMode(talus) && isLookingAtTarget(talus))
                )
        );
    }

    private static int getAttackCooldownDuration(StoneTalus talus) {
        return AnimatedAttacker.optionalCurrentAttackType(talus).map(AttackType::getAttackDuration).orElse(0) + 20;
    }

    private static boolean isLookingAtTarget(StoneTalus talus) {
        LivingEntity target = talus.getTarget();
        if(target != null){
            return COTWUtil.isLookingAt(talus, target, false);
        } else{
            return false;
        }
    }

    private static boolean isInMeleeMode(StoneTalus talus) {
        return !isInRangedMode(talus);
    }

    private static boolean isInRangedMode(StoneTalus talus) {
        return talus.getCurrentAttackType() == StoneTalusAttackType.THROW;
    }


    private static void resetDigCooldown(LivingEntity entity) {
        BrainUtils.setForgettableMemory(entity, MemoryModuleInit.DIG_COOLDOWN.get(), true, 1200);
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<StoneTalus>(
                        new SetAttackTarget<>(false)
                                .targetFinder(talus -> Optional.ofNullable(BrainUtils.getMemory(talus, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER))
                                        .filter(player -> player.closerThan(talus, COTWUtil.getHitboxAdjustedDistance(talus, player, 5)))
                                        .orElse(null)),
                        new SetRetaliateTarget<>()
                                .alertAlliesWhen((talus, target) -> true)
                                .attackablePredicate(target -> !this.isAlliedTo(target)),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>().speedModifier(0.6F),
                        new Idle<>().runFor(talus -> talus.getRandom().nextInt(30, 60))));
    }

    @Override
    public Map<Activity, BrainActivityGroup<? extends StoneTalus>> getAdditionalTasks() {
        return Util.make(new Object2ObjectOpenHashMap<>(), map -> {
            map.put(Activity.EMERGE,
                    new BrainActivityGroup<StoneTalus>(Activity.EMERGE)
                            .behaviours(
                                    new Emerging<>(EMERGE_TICKS)
                                            .finishEmerging(talus -> {
                                                if (talus.hasPose(Pose.EMERGING)) {
                                                    talus.setPose(Pose.STANDING);
                                                    resetDigCooldown(talus);
                                                    COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "Reset dig cooldown for {}!", this);
                                                } else{
                                                    COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "{} was not in emerging pose! Actually in {} pose!", this, talus.getPose().name());
                                                }
                                            })
                            )
                            .requireAndWipeMemoriesOnUse(MemoryModuleType.IS_EMERGING)
            );
            map.put(Activity.DIG,
                    new BrainActivityGroup<StoneTalus>(Activity.DIG)
                            .behaviours(
                                    new Digging<>(DIG_TICKS)
                                            .finishDigging(talus -> {
                                                if(talus.hasPose(Pose.DIGGING)){
                                                    talus.setPose(Pose.SLEEPING);
                                                    BrainUtils.setMemory(talus, MemoryModuleInit.IS_SLEEPING.get(), true);
                                                    COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "Set {} to sleeping after digging!", talus);
                                                } else{
                                                    COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "{} was not in digging pose! Actually in {} pose!", this, talus.getPose().name());
                                                }
                                            })
                                            .whenStarting(talus -> COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "{} started digging!", talus))
                            )
                            .onlyStartWithMemoryStatus(MemoryModuleInit.DIG_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT)
            );
            map.put(Activity.REST,
                    new BrainActivityGroup<StoneTalus>(Activity.REST)
                            .behaviours(
                                    new CustomBehaviour<>(talus -> {
                                        BrainUtils.setForgettableMemory(talus, MemoryModuleType.IS_EMERGING, Unit.INSTANCE, EMERGE_TICKS + 1);
                                        BrainUtils.clearMemory(talus, MemoryModuleInit.IS_SLEEPING.get());
                                    })
                                            .startCondition(talus -> BrainUtils.hasMemory(talus, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER))
                            )
                            .requireAndWipeMemoriesOnUse(MemoryModuleInit.IS_SLEEPING.get())
            );
        });
    }
}
