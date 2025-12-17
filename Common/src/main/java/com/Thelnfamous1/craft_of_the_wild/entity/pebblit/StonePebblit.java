package com.Thelnfamous1.craft_of_the_wild.entity.pebblit;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.*;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior.*;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.BlockDisguiseSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWNearbyPlayersSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.StoneTalusBodyRotationControl;
import com.Thelnfamous1.craft_of_the_wild.init.AttributeInit;
import com.Thelnfamous1.craft_of_the_wild.init.MemoryModuleInit;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.*;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

public class StonePebblit extends COTWMonster<StonePebblitAttackType> implements SmartBrainOwner<StonePebblit>, StoneTalusLike, BlockDisguise {
    public static final float LOGICAL_SCALE = 1F/2F; // desired target is 1
    public static final float VISUAL_SCALE = 1F/2F; // desired target is 1
    protected static final EntityDataAccessor<BlockState> DATA_DISGUISE_BLOCK_STATE = SynchedEntityData.defineId(StonePebblit.class, EntityDataSerializers.BLOCK_STATE);
    protected static final EntityDataAccessor<OptionalInt> DATA_ATTACK_TYPE_ID = SynchedEntityData.defineId(StonePebblit.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    public static final int DROWN_TICKS = COTWUtil.secondsToTicks(3);
    public static final EntityDimensions BLOCK_DISGUISE_DIMENSIONS = EntityDimensions.fixed(1.0F, 1.0F);
    @Nullable private StonePebblitAttackType currentAttackType;

    public static final int MAX_DEATH_TIME = COTWUtil.secondsToTicks(1.5F);
    private final Set<TagKey<Fluid>> fluidsInEye = Sets.newHashSet();
    @Nullable
    public CompoundTag blockData;

    public StonePebblit(EntityType<? extends StonePebblit> type, Level level) {
        super(type, level);
        this.xpReward = 6;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(AttributeInit.PROJECTILE_RESISTANCE.get(), 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    protected void playSoundEvent(SoundEvent soundEvent){
        this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    protected void playStepSound(BlockPos stepPos, BlockState stepState) {
        this.playSoundEvent(SoundInit.STONE_PEBBLIT_WALK.get());
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(!pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && !pSource.is(DamageTypeTags.IS_DROWNING)
                && !pSource.is(DamageTypeTags.IS_EXPLOSION)
                && !isPickaxeDamage(pSource)){
            return false;
        }
        if(pSource.is(DamageTypeTags.IS_PROJECTILE)){
            pAmount -= pAmount * this.getAttributeValue(AttributeInit.PROJECTILE_RESISTANCE.get());
        }
        return super.hurt(pSource, pAmount);
    }

    private static boolean isPickaxeDamage(DamageSource pSource) {
        return pSource.getDirectEntity() instanceof LivingEntity living && living.getMainHandItem().is(ItemTags.PICKAXES);
    }

    @Override
    protected double getAttackRadius(StonePebblitAttackType currentAttackType) {
        return 1.0D;
    }

    @Override
    protected void onAttackStarted(StonePebblitAttackType currentAttackType) {
        if(currentAttackType == StonePebblitAttackType.POUND){
            this.playSoundEvent(SoundInit.STONE_PEBBLIT_POUND.get());
        }
    }

    @Override
    protected void playAttackSound(StonePebblitAttackType currentAttackType, AttackPoint currentAttackPoint) {

    }

    @Override
    protected StonePebblitAttackType selectAttackTypeForTarget(Entity target) {
        return StonePebblitAttackType.POUND;
    }

    @Nullable
    @Override
    public StonePebblitAttackType getCurrentAttackType() {
        if(!this.level().isClientSide){
            return this.currentAttackType;
        } else{
            OptionalInt id = this.entityData.get(DATA_ATTACK_TYPE_ID);
            return id.isPresent() ? StonePebblitAttackType.byId(id.getAsInt()) : null;
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        if(this.hasBlockDisguise()){
            return BLOCK_DISGUISE_DIMENSIONS;
        }
        return super.getDimensions($$0);
    }

    @Override
    public void setCurrentAttackType(@Nullable StonePebblitAttackType attackType, boolean force) {
        this.currentAttackType = attackType;
        this.entityData.set(DATA_ATTACK_TYPE_ID, attackType == null ? OptionalInt.empty() : OptionalInt.of(attackType.getId()), force);
    }

    @Override
    protected int getMaxDeathTime() {
        return MAX_DEATH_TIME;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(COTWAnimations.moveController(this));
        controllers.add(COTWAnimations.poseController(this));
        controllers.add(COTWAnimations.attackController(this));
    }

    @Override
    public boolean refuseToMove(boolean checkAttacking){
        return this.isInsideGround() || this.isDeadOrDying() || checkAttacking && this.isAttackAnimationInProgress();
    }

    public boolean isInsideGround() {
        return this.hasBlockDisguise();
    }

    @Override
    protected boolean isAttackCoolingDown() {
        return BrainUtils.hasMemory(this, MemoryModuleType.ATTACK_COOLING_DOWN);
    }

    @Override
    protected void startAttackCooldown() {
        // NO-OP, already handled with Behaviors setting MemoryModuleType.ATTACK_COOLING_DOWN
    }

    @Nullable
    public LivingEntity getTarget() {
        return BrainUtils.getTargetOfEntity(this);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        COTWUtil.setAttackTarget(this, target);
    }

    protected void clampHeadRotationToBody(Entity entityToUpdate) {
        float yHeadRot = entityToUpdate.getYHeadRot();
        float difference = yHeadRot - Mth.rotateIfNecessary(yHeadRot, this.yBodyRot, (float) this.getMaxHeadYRot());
        entityToUpdate.setYHeadRot(difference);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.refuseToMove(false)) {
            this.clampHeadRotationToBody(this);
        }
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("block_state", NbtUtils.writeBlockState(this.getDisguiseBlockState()));
        if (this.blockData != null) {
            tag.put("TileEntityData", this.blockData);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("block_state", CompoundTag.TAG_COMPOUND)){
            this.setDisguiseBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("block_state")));
        }

        if (tag.contains("TileEntityData", CompoundTag.TAG_COMPOUND)) {
            this.blockData = tag.getCompound("TileEntityData");
        }
        // Doing this here since SBL does not deserialize Brain NBT
        COTWUtil.readBrainFromTag(tag, this);
        /*
        COTWUtil.debugMemoryStatus(Constants.DEBUG_STONE_TALUS, this, MemoryModuleInit.DIG_COOLDOWN.get());
         */
    }

    @Override
    public void fillCrashReportCategory(CrashReportCategory $$0) {
        super.fillCrashReportCategory($$0);
        $$0.setDetail("Immitating BlockState", this.getDisguiseBlockState().toString());
    }

    @Override
    public BlockState getDisguiseBlockState() {
        return this.entityData.get(DATA_DISGUISE_BLOCK_STATE);
    }

    public void setDisguiseBlockState(BlockState pBlockState) {
        this.entityData.set(DATA_DISGUISE_BLOCK_STATE, pBlockState);
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return 0.0F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACK_TYPE_ID, OptionalInt.empty());
        this.entityData.define(DATA_DISGUISE_BLOCK_STATE, Blocks.STONE.defaultBlockState());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if(dataAccessor.equals(DATA_DISGUISE_BLOCK_STATE)){
            this.refreshDimensions();
        }
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
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        COTWCommon.debug(Constants.DEBUG_STONE_PEBBLIT, "Spawned {} at {}", this, this.blockPosition());

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }


    @Override
    protected BodyRotationControl createBodyControl() {
        return new StoneTalusBodyRotationControl<>(this);
    }

    /*
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.getDisguiseBlockState()));
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        this.setBlockState(Block.stateById(pPacket.getData()));
    }
     */

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

    protected boolean isTargetOnTopOfMe(Entity target) {
        if(target.getBoundingBox().minY < this.getBoundingBox().maxY){
            return false;
        }
        return this.getBoundingBox().expandTowards(0, 1, 0).intersects(target.getBoundingBox());
    }

    @Override
    protected boolean isAlliedToDefault(Entity other) {
        return other.getType().is(COTWTags.STONE_PEBBLIT_FRIENDS);
    }

    @Override
    protected void finalizeAreaOfEffectAttack(StonePebblitAttackType currentAttackType, AttackPoint currentAttackPoint, AABB attackBox) {

    }

    // NO-OP to work around the vanilla logic of drowning entities
    @Override
    protected int decreaseAirSupply(int pAir) {
        return pAir;
    }

    // Used to work around the vanilla logic of drowning entities
    protected int loseAirSupply(int air){
        return super.decreaseAirSupply(air);
    }

    @Override
    public int getMaxAirSupply() {
        return DROWN_TICKS;
    }

    @Override
    public void baseTick() {
        super.baseTick();

        this.tickAirSupply();
    }

    protected void tickAirSupply() {
        if (this.isPebblitEyeInFluid(COTWTags.STONE_PEBBLIT_DROWNS_IN) && !this.level().getBlockState(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ())).is(Blocks.BUBBLE_COLUMN)) {
            boolean canDrown = !this.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(this);
            if (canDrown) {
                this.setAirSupply(this.loseAirSupply(this.getAirSupply()));
                if (this.getAirSupply() == -20) {
                    this.setAirSupply(0);
                    this.hurt(this.damageSources().drown(), Float.MAX_VALUE);
                }
            }

            if (!this.level().isClientSide && this.isPassenger() && this.getVehicle() != null && this.getVehicle().dismountsUnderwater()) {
                this.stopRiding();
            }
        } else if (this.getAirSupply() < this.getMaxAirSupply()) {
            this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
        }
    }

    // Fixes a forge-specific issue with how it changes Entity#updateFluidOnEyes
    protected boolean isPebblitEyeInFluid(TagKey<Fluid> fluidTagKey) {
        return this.isEyeInFluid(fluidTagKey) || COTWUtil.updateAndGetFluidOnEyes(this, this.fluidsInEye).contains(fluidTagKey);
    }

    @Override
    public void die(DamageSource $$0) {
        if(!this.isRemoved() && !this.dead){
            this.explodeOnDeath();
        }
        super.die($$0);
    }

    protected void explodeOnDeath() {
        if(!this.level().isClientSide){
            float explosionRadius = (this.getBbWidth() * 6.0F) / 2.0F;
            float explosionDiameter = explosionRadius * 2;
            int k1 = Mth.floor(this.getX() - (double)explosionDiameter - 1.0D);
            int l1 = Mth.floor(this.getX() + (double)explosionDiameter + 1.0D);
            int i2 = Mth.floor(this.getY() - (double)explosionDiameter - 1.0D);
            int i1 = Mth.floor(this.getY() + (double)explosionDiameter + 1.0D);
            int j2 = Mth.floor(this.getZ() - (double)explosionDiameter - 1.0D);
            int j1 = Mth.floor(this.getZ() + (double)explosionDiameter + 1.0D);
            List<Entity> targets = this.level().getEntities(this, new AABB(k1, i2, j2, l1, i1, j1));
            targets.forEach(target -> {
                boolean canTarget = !(target instanceof LivingEntity victim) || COTWAttacker.AREA_OF_EFFECT_TARGETING_CONDITIONS.test(this, victim);
                if(!target.ignoreExplosion() && canTarget){
                    target.hurt(this.level().damageSources().explosion(this, null), 6.0F);
                    this.doPostDamageEffects(target);
                    Vec3 explosionKnockbackVector = COTWUtil.createExplosionKnockbackVector(this.position(), target, explosionDiameter);
                    if(explosionKnockbackVector != null){
                        target.push(explosionKnockbackVector.x, explosionKnockbackVector.y, explosionKnockbackVector.z);
                        target.hurtMarked = true;
                    }
                }
            });
            COTWUtil.spawnVanillaExplosionParticles((ServerLevel) this.level(), explosionRadius, this.position());
            COTWUtil.playVanillaExplosionSound(this, SoundEvents.GENERIC_EXPLODE, 1.0F);
        }
    }

    // BRAIN

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
    public void handleAdditionalBrainSetup(SmartBrain<? extends StonePebblit> brain) {
        brain.setActiveActivityIfPossible(this.hasBlockDisguise() ? Activity.HIDE : Activity.IDLE);
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(Activity.HIDE, Activity.FIGHT, Activity.IDLE);
    }

    @Override
    public List<? extends ExtendedSensor<? extends StonePebblit>> getSensors() {
        return ObjectArrayList.of(
                new COTWNearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new BlockDisguiseSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends StonePebblit> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                COTWSharedAi.createVanillaStyleLookAtTarget(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends StonePebblit> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new CustomBehaviour<>(StonePebblit::updateCurrentAttackTypeForTarget),
                new InvalidateAttackTarget<StonePebblit>().invalidateIf((talus, target) -> !talus.closerThan(target, COTWUtil.getHitboxAdjustedDistance(talus, target, getFollowingRange(talus)))),
                new LookAtAttackTarget<>(), // need this so the talus always tries to look at the attack target even if it is within the attack range
                new COTWSetWalkTargetToAttackTarget<StonePebblit>()
                        .isWithinAttackRange((talus, target) -> talus.isWithinMeleeAttackRange(target, 1))
                        .speedMod((talus, target) -> 1.0F)
                        .startCondition(StonePebblit::isInMeleeMode),
                new FirstApplicableBehaviour<>(
                        new COTWAnimatableMeleeAttack<StonePebblit>(0)
                                .isWithinMeleeAttackRange((talus, target) -> talus.isWithinMeleeAttackRange(target, 1))
                                .attackInterval(StonePebblit::getAttackCooldownDuration)
                                .startCondition(StonePebblit::isInMeleeMode)
                )
        );
    }

    @Override
    public Map<Activity, BrainActivityGroup<? extends StonePebblit>> getAdditionalTasks() {
        return Util.make(new Object2ObjectOpenHashMap<>(), map -> {
            map.put(Activity.HIDE,
                    new BrainActivityGroup<StonePebblit>(Activity.HIDE)
                            .behaviours(
                                    new CustomBehaviour<StonePebblit>(pebblit -> {
                                        BlockState disguiseBlockState = pebblit.getDisguiseBlockState();
                                        if(disguiseBlockState.shouldSpawnParticlesOnBreak()){
                                            pebblit.level().levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, pebblit.blockPosition(), Block.getId(disguiseBlockState));
                                        }
                                        pebblit.setDisguiseBlockState(Blocks.AIR.defaultBlockState());
                                        BrainUtils.clearMemory(pebblit, MemoryModuleInit.IS_DISGUISED.get());
                                    })
                                            .startCondition(talus -> COTWUtil.getOptionalMemory(this, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                                                    .filter(player -> player.closerThan(talus, COTWUtil.getHitboxAdjustedDistance(talus, player, getDetectingRange())))
                                                    .isPresent())
                            )
                            .requireAndWipeMemoriesOnUse(MemoryModuleInit.IS_DISGUISED.get())
            );
        });
    }

    private static double getFollowingRange(StonePebblit talus) {
        return COTWUtil.getFollowRange(talus);
    }

    private static int getAttackCooldownDuration(StonePebblit talus) {
        return AnimatedAttacker.optionalCurrentAttackType(talus).map(AttackType::getAttackDuration).orElse(0) + 20;
    }

    private static boolean isInMeleeMode(StonePebblit talus) {
        return talus.getCurrentAttackType() == StonePebblitAttackType.POUND;
    }

    @Override
    public BrainActivityGroup<? extends StonePebblit> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                firstApplicableIdleTask(this),
                randomIdleTask());
    }

    @NotNull
    private static OneRandomBehaviour<PathfinderMob> randomIdleTask() {
        return new OneRandomBehaviour<>(
                new SetRandomWalkTarget<>().speedModifier(0.6F),
                new Idle<>().runFor(talus -> talus.getRandom().nextInt(30, 60)));
    }

    @NotNull
    private static FirstApplicableBehaviour<StonePebblit> firstApplicableIdleTask(StonePebblit talus) {
        return new FirstApplicableBehaviour<>(
                new SetAttackTarget<StonePebblit>(false)
                        .attackPredicate(st -> {
                            Player attackablePlayer = BrainUtils.getMemory(st, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                            if(attackablePlayer == null) return false;
                            return attackablePlayer.closerThan(st, COTWUtil.getHitboxAdjustedDistance(st, attackablePlayer, getTargetingRange(st)));
                        })
                        .targetFinder(st -> COTWUtil.getOptionalMemory(st, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                                .filter(player -> player.closerThan(st, COTWUtil.getHitboxAdjustedDistance(st, player, getTargetingRange(st))))
                                .orElse(null)),
                new SetRetaliateTarget<>()
                        .alertAlliesWhen((st, target) -> true)
                        .attackablePredicate(target -> !talus.isAlliedTo(target)),
                new SetPlayerLookTarget<>(),
                new SetRandomLookTarget<>());
    }

    protected static double getTargetingRange(StonePebblit talus) {
        return COTWUtil.getFollowRange(talus);
    }

    private static int getDetectingRange() {
        return 5;
    }
}
