package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior.*;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.navigation.COTWGroundPathNavigation;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWNearbyPlayersSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.SleepSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.init.AttributeInit;
import com.Thelnfamous1.craft_of_the_wild.init.MemoryModuleInit;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
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
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomDelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomHeldBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.*;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class StoneTalus extends COTWMonster<StoneTalusAttackType> implements BossMusicPlayer, SmartBrainOwner<StoneTalus>, COTWMultipartEntity, StoneTalusBase, RangedAttackMob {
    public static final float LOGICAL_SCALE = 7F/3F; // desired target is 7
    public static final float VISUAL_SCALE = 7F/6F; // desired target is 7
    public static final float FACEPLANT_ROTATION = 85.0F;
    protected static final EntityDataAccessor<OptionalInt> DATA_ATTACK_TYPE_ID = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    protected static final EntityDataAccessor<Integer> DATA_LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_FACEPLANTED = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.BOOLEAN);
    public static final int EMERGE_TICKS = COTWUtil.secondsToTicks(2.8333F);
    public static final int DIG_TICKS = COTWUtil.secondsToTicks(6.2083F);
    public static final int DEATH_TICKS = COTWUtil.secondsToTicks(2.5F);
    public static final int WITHER_SHOOT_EVENT_ID = 1024;
    private final Entity[] partEntities;
    private final PartEntityController<StoneTalus, ? extends Entity> partEntityController;
    private final ServerBossEvent bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
    @Nullable
    private StoneTalusAttackType currentAttackType;
    @Nullable
    private Variant cachedVariant;
    private int stunCooldown;

    public StoneTalus(EntityType<? extends StoneTalus> type, Level level) {
        super(type, level);
        this.setMaxUpStep(2.0F); // Talus can step up 2 blocks without triggering a jump
        this.xpReward = 60;
        this.partEntityController = Services.PLATFORM.makePartEntityController(this,
                StoneTalus::tickParts,
                StoneTalus::resizeParts,
                new PartEntityController.PartInfo("weakPoint", 0.6875F, 0.6875F, true, 0, 2.9375, -0.5, StoneTalus.LOGICAL_SCALE),
                new PartEntityController.PartInfo("head", 3.125F, 1.125F, true, 0, 1.8125, 0, StoneTalus.LOGICAL_SCALE),
                new PartEntityController.PartInfo("body", 2.625F, 1.0625F, true, 0, 0.75, 0, StoneTalus.LOGICAL_SCALE),
                new PartEntityController.PartInfo("leftArm", 1.125F, 1.5625F, true, -1.6875, 0, 0, StoneTalus.LOGICAL_SCALE),
                new PartEntityController.PartInfo("rightArm", 1.125F, 1.5625F, true, 1.6875, 0, 0, StoneTalus.LOGICAL_SCALE),
                new PartEntityController.PartInfo("leftLeg", 0.5625F, 0.75F, true, -0.5, 0, 0, StoneTalus.LOGICAL_SCALE),
                new PartEntityController.PartInfo("rightLeg", 0.5625F, 0.75F, true, 0.5, 0, 0, StoneTalus.LOGICAL_SCALE));
        this.partEntities = this.partEntityController.collectParts().toArray(Entity[]::new);
        // Forge: Fix MC-158205: Make sure part ids are successors of parent mob id
        this.setId(EntityAccessor.craft_of_the_wild$getENTITY_COUNTER().getAndAdd(this.partEntities.length + 1) + 1);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new COTWGroundPathNavigation(this, level);
    }

    public static boolean canDestroy(BlockState blockState) {
        return blockState.is(COTWTags.STONE_TALUS_CAN_DESTROY);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(AttributeInit.PROJECTILE_RESISTANCE.get(), 0.7D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    private static void tickParts(Entity part, StoneTalus talus, PartEntityController.PartInfo partInfo){
        COTWPartEntity.basicTicker(part, talus, partInfo, talus.isFaceplanting());
    }

    private static EntityDimensions resizeParts(Entity part, StoneTalus talus,  EntityDimensions defaultSize){
        if(talus.isFaceplanted()){
            return EntityDimensions.scalable(defaultSize.height, defaultSize.width).scale(talus.getScale()); // since it becomes horizontal, switch bounding box dimensions
        } else{
            return defaultSize.scale(talus.getScale());
        }
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
    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT_ID, variant.getId());
    }

    @Override
    public Variant getVariant() {
        if(this.cachedVariant == null){
            this.cachedVariant = Variant.byId(this.entityData.get(DATA_VARIANT_ID));
        }
        return this.cachedVariant;
    }

    @Override
    public boolean canRotateDuringAttack(StoneTalusAttackType currentAttackType) {
        return currentAttackType == StoneTalusAttackType.THROW;
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

        this.setVariant(Util.getRandom(Variant.values(), pLevel.getRandom()));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        EntityDimensions baseDimensions = super.getDimensions(Pose.STANDING);
        if(this.isInsideGround()){
            return EntityDimensions.fixed(baseDimensions.width, 1.125F * LOGICAL_SCALE * this.getScale());
        } else if(this.isFaceplanted()){
            return EntityDimensions.fixed(baseDimensions.height, 2.0625F * LOGICAL_SCALE * this.getScale()); // since it becomes horizontal, invert bounding box dimensions
        } else{
            return super.getDimensions(pPose);
        }
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
        this.entityData.define(DATA_VARIANT_ID, Variant.NORMAL.getId());
        this.entityData.define(DATA_FACEPLANTED, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if(pKey.equals(DATA_FACEPLANTED)){
            this.refreshDimensions();
        }
        if(pKey.equals(DATA_VARIANT_ID)){
            this.cachedVariant = null;
        }
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        for(Entity partEntity : this.partEntities){
            partEntity.refreshDimensions();
        }
    }

    public boolean isFaceplanted(){
        return this.entityData.get(DATA_FACEPLANTED);
    }

    public void setFaceplanted(boolean faceplanted){
        this.entityData.set(DATA_FACEPLANTED, faceplanted);
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
        this.readVariantFromTag(tag);
        // Doing this here since SBL does not deserialize Brain NBT
        COTWUtil.readBrainFromTag(tag, this);
        COTWUtil.debugMemoryStatus(Constants.DEBUG_STONE_TALUS, this, MemoryModuleInit.IS_SLEEPING.get());
        COTWUtil.debugMemoryStatus(Constants.DEBUG_STONE_TALUS, this, MemoryModuleInit.DIG_COOLDOWN.get());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sleeping", this.hasPose(Pose.SLEEPING));
        this.writeVariantToTag(tag);
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
        if(this.stunCooldown > 0){
            this.stunCooldown--;
        }
        if(!this.level().isClientSide && AnimatedAttacker.hasCurrentAttackType(this, StoneTalusAttackType.THROW) && this.getTicksSinceAttackStarted() == COTWUtil.secondsToTicks(3)){
            this.playSoundEvent(SoundInit.STONE_TALUS_REGENERATE_ARMS.get());
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "{} is playing the {} sound!", this, SoundInit.STONE_TALUS_REGENERATE_ARMS.get().getLocation());
        }
        if (this.refuseToMove(false)) {
            this.clampHeadRotationToBody(this);
        }
        if (!this.level().isClientSide) {
            if (this.canPlayBossMusic()) {
                this.level().broadcastEntityEvent(this, MUSIC_PLAY_ID);
            }
            else {
                this.level().broadcastEntityEvent(this, MUSIC_STOP_ID);
            }
        }
    }

    protected void clampHeadRotationToBody(Entity entityToUpdate) {
        float yHeadRot = entityToUpdate.getYHeadRot();
        float difference = yHeadRot - Mth.rotateIfNecessary(yHeadRot, this.yBodyRot, (float) this.getMaxHeadYRot());
        entityToUpdate.setYHeadRot(difference);
    }

    @Override
    public boolean canPlayBossMusic() {
        return !this.isSilent() && this.getTarget() instanceof Player;
    }

    @Override
    public SoundEvent getBossMusic() {
        return SoundInit.STONE_TALUS_BOSS_MUSIC.get();
    }

    @Override
    public void handleEntityEvent(byte eventId) {
        if (eventId == BossMusicPlayer.MUSIC_PLAY_ID) {
            COTWCommon.playBossMusicFor(this);
        }
        if (eventId == BossMusicPlayer.MUSIC_STOP_ID) {
            COTWCommon.stopBossMusicFor(this);
        }
        super.handleEntityEvent(eventId);
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
    public void setCurrentAttackType(@Nullable StoneTalusAttackType attackType, boolean force) {
        this.currentAttackType = attackType;
        this.entityData.set(DATA_ATTACK_TYPE_ID, attackType == null ? OptionalInt.empty() : OptionalInt.of(attackType.getId()), force);
    }

    @Override
    protected void onAttackStarted(StoneTalusAttackType currentAttackType) {
        switch (currentAttackType){
            case HEADBUTT -> this.playSoundEvent(SoundInit.STONE_TALUS_HEADBUTT.get());
            case STUN -> this.playSoundEvent(SoundInit.STONE_TALUS_STUN.get());
            case PUNCH -> this.playSoundEvent(SoundInit.STONE_TALUS_PUNCH.get());
            case POUND -> this.playSoundEvent(SoundInit.STONE_TALUS_POUND.get());
            case THROW -> this.playSoundEvent(SoundInit.STONE_TALUS_THROW_ARMS.get());
            case SHAKE -> this.playSoundEvent(SoundInit.STONE_TALUS_SHAKE.get());
        }
    }

    protected void playSoundEvent(SoundEvent soundEvent){
        this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    protected void playAttackSound(StoneTalusAttackType currentAttackType, AttackPoint currentAttackPoint) {
        if(!this.level().isClientSide){
            if(currentAttackPoint.damageMode() == AttackPoint.DamageMode.AREA_OF_EFFECT){
                COTWUtil.playVanillaExplosionSound(this);
            }
        }
    }

    @Override
    protected float getSoundVolume() {
        return 4.0F;
    }

    @Override
    protected boolean isAttackCoolingDown() {
        return BrainUtils.hasMemory(this, MemoryModuleType.ATTACK_COOLING_DOWN);
    }

    @Override
    protected void startAttackCooldown() {
        // NO-OP, already handled with Behaviors setting MemoryModuleType.ATTACK_COOLING_DOWN
    }

    @Override
    protected double getAttackRadius(StoneTalusAttackType currentAttackType) {
        switch (currentAttackType){
            case HEADBUTT, STUN -> {
                return 2 * LOGICAL_SCALE * this.getScale(); // diameter = 4, scaled up by 7/3 to be 28/3 (9 + 1/3)
            }
            case POUND -> {
                return 1.5 * LOGICAL_SCALE * this.getScale(); // diameter = 3, scaled up by 7/3 to be 21/3 (7)
            }
            case PUNCH -> {
                return 1.5 * LOGICAL_SCALE * this.getScale(); // diameter = 2, scaled up by 7/3 to be 14/3 (4 + 2/3)
            }
            default -> {
                return 0;
            }
        }
    }

    @Override
    protected StoneTalusAttackType selectAttackTypeForTarget(Entity target) {
        if(this.isTargetOnTopOfMe(target)){
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_SHAKE, "{} is now trying to shake off {}", this, target);
            return StoneTalusAttackType.SHAKE;
        }

        if(this.shouldUseRangedAttack(target)){
            return StoneTalusAttackType.THROW;
        }
        return this.selectMeleeAttackType();
    }

    private boolean isTargetOnTopOfMe(Entity target) {
        if(target.getBoundingBox().minY < this.getBoundingBox().maxY){
            return false;
        }
        return this.getBoundingBox().expandTowards(0, 1, 0).intersects(target.getBoundingBox());
    }

    private StoneTalusAttackType selectMeleeAttackType() {
        if(Constants.DEBUG_STONE_TALUS_HEADBUTT){
            return StoneTalusAttackType.HEADBUTT;
        }
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
        boolean shouldShake = this.isTargetOnTopOfMe(target);
        if(currentAttackType != StoneTalusAttackType.SHAKE && shouldShake){
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_SHAKE, "{} is now trying to shake off {}", this, target);
            this.setCurrentAttackType(StoneTalusAttackType.SHAKE, false);
            return;
        }
        boolean shouldUseRangedAttack = this.shouldUseRangedAttack(target);
        if(currentAttackType == StoneTalusAttackType.THROW && !shouldUseRangedAttack){
            this.setCurrentAttackType(this.selectMeleeAttackType(), false);
        } else if(currentAttackType != StoneTalusAttackType.THROW && shouldUseRangedAttack){
            this.setCurrentAttackType(StoneTalusAttackType.THROW, false);
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
                case 0 -> armPosition = armPosition.add(COTWUtil.yRotatedXZVector(-1, 1, this.getYHeadRot()).scale(LOGICAL_SCALE));
                case 1 -> armPosition = armPosition.add(COTWUtil.yRotatedXZVector(1, 1, this.getYHeadRot()).scale(LOGICAL_SCALE));
            }

            double x = armPosition.x;
            double y = armPosition.y;
            double z = armPosition.z;
            double xDist = targetX - x;
            double yDist = targetY - y;
            double zDist = targetZ - z;
            StoneTalusArm stoneTalusArm = new StoneTalusArm(this.level(), this, xDist, yDist, zDist);
            stoneTalusArm.setVariant(this.getVariant());
            stoneTalusArm.setBaseDamage(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * currentAttackPoint.baseDamageModifier());
            stoneTalusArm.setRadius(1 * LOGICAL_SCALE * this.getScale()); // diameter = 2, scaled up by 7/3 to be 14/3 (4 + 2/3)

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
        return SoundInit.STONE_TALUS_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.STONE_TALUS_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos stepPos, BlockState stepState) {
        this.playSoundEvent(SoundInit.STONE_TALUS_WALK.get());
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
    protected void doCustomAttack(StoneTalusAttackType currentAttackType, AttackPoint currentAttackPoint) {
        if(currentAttackType == StoneTalusAttackType.SHAKE){
            double xZAttackRadius = this.getBbWidth() * 0.5F;
            double yAttackRadius = 0.5F;
            double xZAttackSize = xZAttackRadius * 2;
            double yAttackSize = xZAttackRadius * 2;
            AABB attackBox = AABB.ofSize(this.position().add(0, this.getBbHeight(), 0).add(0, yAttackRadius, 0), xZAttackSize, yAttackSize, xZAttackSize);
            List<LivingEntity> targets = this.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, attackBox);
            targets.forEach(target -> {
                float yRot = this.getRandom().nextFloat() * 360F;
                Vec3 pushVec = new Vec3(0, 0, -1).yRot(-yRot * Mth.DEG_TO_RAD).add(0, 0.2, 0);
                Vec3 existingMovement = target.getDeltaMovement();
                target.setDeltaMovement(existingMovement.x / 2.0D + pushVec.x, existingMovement.y / 2.0D + pushVec.y, existingMovement.z / 2.0D + pushVec.z);
                target.hurtMarked = true;
            });
        }
    }

    @Override
    public boolean isPickable() {
        // Need to return false so the regular hitbox is not used for hit detection
        // Not needed on Fabric, as this mob will not have sub-parts until a Fabric subpart API is available
        return Services.PLATFORM.getPlatformName().equals("Forge");
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
            if(pSource.is(DamageTypeTags.IS_PROJECTILE) && !this.isStunned()){
                this.stun();
            }
            return this.reallyHurt(pSource, pDamage);
        } else if(pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS, "Damaging {} with non-weakpoint damage {}, {}", this, pSource, pDamage);
            return this.reallyHurt(pSource, pDamage);
        }
        return false;
    }

    private boolean isStunned() {
        return AnimatedAttacker.hasCurrentAttackType(this, StoneTalusAttackType.STUN);
    }

    private void stun() {
        if(this.stunCooldown <= 0){
            this.startAttack(() -> StoneTalusAttackType.STUN, true);
            // manually set attack cooling down memory for STUN since it is not set by the main attacking Behaviors
            BrainUtils.setForgettableMemory(this, MemoryModuleType.ATTACK_COOLING_DOWN, true, getAttackCooldownDuration(this));
            this.stunCooldown = StoneTalusAttackType.STUN.getAttackDuration() + COTWUtil.secondsToTicks(6F);
        }

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
                new COTWNearbyPlayersSensor<>(),
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
                new MoveToWalkTarget<>(),
                new CustomHeldBehaviour<>(StoneTalus::updateXRotForFaceplant)
                        .startCondition(StoneTalus::isFaceplanting)
                        .stopIf(Predicate.not(StoneTalus::isFaceplanting))
                        .whenStarting(talus -> talus.setXRot(0.0F))
                        .whenStopping(talus -> {
                            talus.setFaceplanted(false);
                            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_FACEPLANT, "{} is forced to no longer faceplanted!", talus);
                        })
        );
    }

    private boolean isFaceplanting() {
        return this.isAttackAnimationInProgress() && (this.isHeadbutting() || this.isStunned());
    }

    private boolean isHeadbutting() {
        return AnimatedAttacker.hasCurrentAttackType(this, StoneTalusAttackType.HEADBUTT);
    }

    private static void updateXRotForFaceplant(StoneTalus talus){
        int ticksSinceAttackStarted = talus.getTicksSinceAttackStarted();
        boolean headbuttAttack = talus.isHeadbutting();
        int fallStart = headbuttAttack ? COTWUtil.secondsToTicks(1.96F) : COTWUtil.secondsToTicks(1.5F);
        int fallEnd = headbuttAttack ? COTWUtil.secondsToTicks(2.25F) : COTWUtil.secondsToTicks(2.71F);
        if(ticksSinceAttackStarted >= fallStart && ticksSinceAttackStarted <= fallEnd){
            float tickDelta = COTWUtil.getTickDelta(FACEPLANT_ROTATION, Math.abs(fallEnd - fallStart));
            talus.setXRot(Math.min(talus.getXRot() + tickDelta, FACEPLANT_ROTATION));
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_FACEPLANT, "{} is faceplanting! X-Rotation is {}", talus, talus.getXRot());
        } else {
            int riseStart = headbuttAttack ? COTWUtil.secondsToTicks(4.58F) : COTWUtil.secondsToTicks(5.21F);
            int riseEnd = headbuttAttack ? COTWUtil.secondsToTicks(5.17F) : COTWUtil.secondsToTicks(6.4583F);
            if(ticksSinceAttackStarted >= riseStart && ticksSinceAttackStarted <= riseEnd){
                float tickDelta = COTWUtil.getTickDelta(FACEPLANT_ROTATION, Math.abs(riseEnd - riseStart));
                talus.setXRot(Math.max(talus.getXRot() - tickDelta, 0.0F));
                COTWCommon.debug(Constants.DEBUG_STONE_TALUS_FACEPLANT, "{} is unfaceplanting! X-Rotation is {}", talus, talus.getXRot());
            }
        }
        if(talus.getXRot() == FACEPLANT_ROTATION && !talus.isFaceplanted()){
            talus.setFaceplanted(true);
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_FACEPLANT, "{} is now faceplanted!", talus);
        } else if(talus.getXRot() == 0.0F && talus.isFaceplanted()){
            talus.setFaceplanted(false);
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_FACEPLANT, "{} is no longer faceplanted!", talus);
        }
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new CustomBehaviour<>(StoneTalus::resetDigCooldown),
                new CustomBehaviour<>(StoneTalus::updateCurrentAttackTypeForTarget),
                new InvalidateAttackTarget<StoneTalus>().invalidateIf((talus, target) -> !talus.closerThan(target, COTWUtil.getHitboxAdjustedDistance(talus, target, getFollowingRange(talus)))),
                new LookAtAttackTarget<>(), // need this so the talus always tries to look at the attack target even if it is within the attack range
                new COTWSetWalkTargetToAttackTarget<StoneTalus>()
                        .isWithinAttackRange((talus, target) -> talus.isWithinMeleeAttackRange(target, 1))
                        .speedMod((talus, target) -> 1.0F)
                        .startCondition(StoneTalus::isInMeleeMode),
                new FirstApplicableBehaviour<>(
                        new CustomDelayedBehaviour<StoneTalus>(COTWUtil.secondsToTicks(3F))
                                .whenActivating(talus -> {
                                    talus.startAttack(() -> StoneTalusAttackType.SHAKE, true);
                                    // manually set attack cooling down memory for SHAKE since it is not set by the main attacking Behaviors
                                    BrainUtils.setForgettableMemory(this, MemoryModuleType.ATTACK_COOLING_DOWN, true, getAttackCooldownDuration(this));
                                })
                                .startCondition(StoneTalus::isPreShakeOffTarget)
                                .stopIf(talus -> !isPreShakeOffTarget(talus)),
                        new COTWAnimatableRangedAttack<StoneTalus>(0)
                                .getPerceivedTargetDistanceSquared(COTWUtil::getDistSqrBetweenHitboxes)
                                .attackRadius(64)
                                .attackInterval(StoneTalus::getAttackCooldownDuration)
                                .startCondition(StoneTalus::isInRangedMode),
                        new COTWAnimatableMeleeAttack<StoneTalus>(0)
                                .isWithinMeleeAttackRange((talus, target) -> talus.isWithinMeleeAttackRange(target, 1))
                                .attackInterval(StoneTalus::getAttackCooldownDuration)
                                .startCondition(StoneTalus::isInMeleeMode)
                )
        );
    }

    private static boolean isPreShakeOffTarget(StoneTalus talus) {
        if(!AnimatedAttacker.hasCurrentAttackType(talus, StoneTalusAttackType.SHAKE)) return false;
        return !talus.isAttackAnimationInProgress() && !talus.isAttackCoolingDown();
    }

    private static double getFollowingRange(StoneTalus talus) {
        return COTWUtil.getFollowRange(talus);
    }

    private static int getAttackCooldownDuration(StoneTalus talus) {
        return AnimatedAttacker.optionalCurrentAttackType(talus).map(AttackType::getAttackDuration).orElse(0) + 20;
    }

    private static boolean isInMeleeMode(StoneTalus talus) {
        return !isInRangedMode(talus) && !talus.isStunned();
    }

    private static boolean isInRangedMode(StoneTalus talus) {
        return talus.getCurrentAttackType() == StoneTalusAttackType.THROW && !talus.isStunned();
    }


    private static void resetDigCooldown(LivingEntity entity) {
        BrainUtils.setForgettableMemory(entity, MemoryModuleInit.DIG_COOLDOWN.get(), true, 1200);
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new SetAttackTarget<StoneTalus>(false)
                                .targetFinder(talus -> COTWUtil.getOptionalMemory(this, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                                        .filter(player -> player.closerThan(talus, COTWUtil.getHitboxAdjustedDistance(talus, player, getTargetingRange(talus))))
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

    private static double getTargetingRange(StoneTalus talus) {
        return COTWUtil.getFollowRange(talus);
    }

    @Override
    public Map<Activity, BrainActivityGroup<? extends StoneTalus>> getAdditionalTasks() {
        return Util.make(new Object2ObjectOpenHashMap<>(), map -> {
            map.put(Activity.EMERGE,
                    new BrainActivityGroup<StoneTalus>(Activity.EMERGE)
                            .behaviours(
                                    new Emerging<StoneTalus>(EMERGE_TICKS)
                                            .startEmerging(talus -> {
                                                talus.setPose(Pose.EMERGING);
                                                talus.playSoundEvent(SoundInit.STONE_TALUS_SPAWN.get());
                                            })
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
                                            .startCondition(talus -> COTWUtil.getOptionalMemory(this, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                                                    .filter(player -> player.closerThan(talus, COTWUtil.getHitboxAdjustedDistance(talus, player, getDetectingRange())))
                                                    .isPresent())
                            )
                            .requireAndWipeMemoriesOnUse(MemoryModuleInit.IS_SLEEPING.get())
            );
        });
    }

    private static int getDetectingRange() {
        return 5;
    }
}
