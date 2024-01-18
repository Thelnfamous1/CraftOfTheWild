package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.Digging;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.Emerging;
import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.init.MemoryModuleInit;
import com.Thelnfamous1.craft_of_the_wild.mixin.EntityAccessor;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
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
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
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

public class StoneTalus extends COTWMonster<StoneTalusAttackType> implements SmartBrainOwner<StoneTalus>, MultipartEntity {
    public static final float SCALE = 7F / 3F;
    protected static final EntityDataAccessor<OptionalInt> DATA_ATTACK_TYPE_ID = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    protected static final EntityDataAccessor<Integer> DATA_LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_BATTLE = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_WALKING = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.BOOLEAN);
    public static final int EMERGE_TICKS = COTWUtil.secondsToTicks(2.8333F);
    public static final int DIG_TICKS = COTWUtil.secondsToTicks(6.2083F);
    public static final int DEATH_TICKS = COTWUtil.secondsToTicks(2.5F);
    private final Entity[] partEntities;
    private final PartEntityController<? extends Entity> partEntityController;
    @Nullable
    private StoneTalusAttackType currentAttackType;

    public StoneTalus(EntityType<? extends StoneTalus> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
        this.xpReward = 60;
        this.partEntityController = Services.PLATFORM.makePartEntityController(this,
                new PartEntityController.Info("weak_point", 0.6875F, 0.6875F, true, 0, 2.9375, 0, StoneTalus.SCALE),
                new PartEntityController.Info("head", 3.125F, 1.125F, true, 0, 1.8125, 0, StoneTalus.SCALE),
                new PartEntityController.Info("body", 2.625F, 1.0625F, true, 0, 0.75, 0, StoneTalus.SCALE),
                new PartEntityController.Info("leftArm", 1.125F, 1.5625F, true, -1.125, 0, 0, StoneTalus.SCALE),
                new PartEntityController.Info("rightArm", 1.125F, 1.5625F, true, 1.125, 0, 0, StoneTalus.SCALE),
                new PartEntityController.Info("leftLeg", 0.5625F, 0.75F, true, -0.25, 0, 0, StoneTalus.SCALE),
                new PartEntityController.Info("rightLeg", 0.5625F, 0.75F, true, 0.25, 0, 0, StoneTalus.SCALE));
        this.partEntities = this.partEntityController.getParts().toArray(Entity[]::new);
        // Forge: Fix MC-158205: Make sure part ids are successors of parent mob id
        this.setId(EntityAccessor.craft_of_the_wild$getENTITY_COUNTER().getAndAdd(this.partEntities.length + 1) + 1);
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

    public static AttributeSupplier.Builder createAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
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
        return this.getCurrentAttackType() == StoneTalusAttackType.PUNCH;
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
            BrainUtils.setMemory(this, MemoryModuleInit.IS_SLEEPING.get(), Unit.INSTANCE);
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
        this.entityData.define(DATA_WALKING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
    }

    public boolean isBattle() {
        return this.entityData.get(DATA_BATTLE);
    }

    public void setBattle(boolean battle){
        this.entityData.set(DATA_BATTLE, battle);
    }

    public boolean isWalking(){
        return this.entityData.get(DATA_WALKING);
    }

    protected void setIsWalking(boolean isWalking){
        this.entityData.set(DATA_WALKING, isWalking);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        tag.putBoolean("Sleeping", this.hasPose(Pose.SLEEPING));
        tag.putBoolean("Battle", this.isBattle());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(tag.contains("Sleeping", Tag.TAG_ANY_NUMERIC) && tag.getBoolean("Sleeping")){
            this.setPose(Pose.SLEEPING);
        }
        if(tag.contains("Battle", Tag.TAG_ANY_NUMERIC)){
            this.setBattle(tag.getBoolean("Battle"));
        }
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
        if (this.refuseToMove() && this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
            pTravelVector = pTravelVector.multiply(0.0D, 1.0D, 0.0D);
        }
        super.travel(pTravelVector);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.refuseToMove()) {
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
        //Constants.LOG.info("Set current attack type to {} for {}", attackType == null ? null : attackType.getSerializedName(), this.getName().getString());
        this.currentAttackType = attackType;
        this.entityData.set(DATA_ATTACK_TYPE_ID, attackType == null ? OptionalInt.empty() : OptionalInt.of(attackType.getId()));
    }

    @Override
    protected void playAttackSound(StoneTalusAttackType currentAttackType) {
        if(!this.level().isClientSide){
            if(currentAttackType.getDamageMode() == DamageMode.AREA_OF_EFFECT){
                this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            } else{
                this.playSound(SoundEvents.IRON_GOLEM_ATTACK);
            }
        }
    }

    @Override
    protected double getAttackRadius(StoneTalusAttackType currentAttackType) {
        switch (currentAttackType){
            case HEADBUTT -> {
                return 3.5;
            }
            case POUND, PUNCH -> {
                return 1.5;
            }
            default -> {
                return 0;
            }
        }
    }

    @Override
    protected StoneTalusAttackType selectAttackType(Entity target) {
        int nextInt = this.level().random.nextInt(9);
        if(nextInt == 0){ // 1 in 9 chance for headbutt
            return StoneTalusAttackType.HEADBUTT;
        } else if(nextInt < 5){ // 4 in 9 chance for pound
            return StoneTalusAttackType.POUND;
        } else{ // 4 in 9 chance for punch
            return StoneTalusAttackType.PUNCH;
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

    public boolean refuseToMove(){
        return this.isInsideGround() || this.isAttackAnimationInProgress() || this.isDeadOrDying();
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
        if(!this.level().isClientSide){
            this.setIsWalking(this.zza > 0);
        }
        this.partEntityController.tick();
    }

    @Override
    public boolean isPickable() {
        return false; // Need to return false so the regular hitbox is not used for hit detection
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !this.level().isClientSide && this.partEntityController.getOptionalPart("head")
                .map(head -> this.hurt(head, source, amount))
                .orElseGet(() -> this.reallyHurt(source, amount));
    }

    @Override
    public boolean hurt(Entity partEntity, DamageSource pSource, float pDamage) {
        if(partEntity == this.partEntityController.getPart("weak_point")){
            Constants.LOG.info("Hit weakpoint for {}", this.getName().getString());
            return this.reallyHurt(pSource, pDamage);
        } else if(pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
            Constants.LOG.info("Damaging {} with non-weakpoint damage", this.getName().getString());
            return this.reallyHurt(pSource, pDamage);
        }
        return false;
    }

    protected boolean reallyHurt(DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(COTWAnimations.moveController(this));
        controllers.add(COTWAnimations.poseController(this));
        controllers.add(COTWAnimations.attackController(this));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(this.hasPose(Pose.SLEEPING) && this.tickCount % 20 == 0){
            this.heal(1.0F);
        }
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
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(Activity.EMERGE, Activity.REST, Activity.DIG, Activity.FIGHT, Activity.IDLE);
    }

    @Override
    public void handleAdditionalBrainSetup(SmartBrain<? extends StoneTalus> brain) {
        brain.getMemories().put(MemoryModuleType.DIG_COOLDOWN, Optional.empty());
        brain.getMemories().put(MemoryModuleInit.IS_SLEEPING.get(), Optional.empty());
    }

    @Override
    public List<? extends ExtendedSensor<? extends StoneTalus>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>()
                        .stopIf(talus -> talus.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter(pt -> pt.isVisibleBy(talus)).isEmpty())
                        .whenStopping(talus -> BrainUtils.clearMemory(talus, MemoryModuleType.LOOK_TARGET))
                        .runFor(talus -> talus.getRandom().nextIntBetweenInclusive(40, 90)),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new CustomBehaviour<>(StoneTalus::resetDigCooldown),
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>().speedMod((talus, target) -> 1.0F),
                new AnimatableMeleeAttack<StoneTalus>(0)
                        .attackInterval(talus -> talus.getOptionalCurrentAttackType().map(AttackType::getAttackAnimationLength).orElse(0))
                        .whenStarting(talus -> talus.setCurrentAttackType(talus.selectAttackType(talus.getTarget()))) // called before attackInterval
        );
    }

    private static void resetDigCooldown(LivingEntity entity) {
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200);
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
                                                    Constants.LOG.info("Reset dig cooldown!");
                                                } else{
                                                    Constants.LOG.info("Was not in emerging pose! Actually in {} pose!", talus.getPose().name());
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
                                                    BrainUtils.setMemory(talus, MemoryModuleInit.IS_SLEEPING.get(), Unit.INSTANCE);
                                                }
                                            })
                            )
                            .onlyStartWithMemoryStatus(MemoryModuleType.DIG_COOLDOWN, MemoryStatus.VALUE_ABSENT)
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
