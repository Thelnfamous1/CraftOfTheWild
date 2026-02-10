package com.Thelnfamous1.craft_of_the_wild.entity.trader;

import com.Thelnfamous1.craft_of_the_wild.entity.COTWMob;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior.*;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWNearbyPlayersSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.SleepSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.entity.trader.trades.COTWVillagerTrades;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.Thelnfamous1.craft_of_the_wild.init.VillagerProfessionInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;
import java.util.Map;

public class Beedle extends COTWTrader implements SmartBrainOwner<Beedle> {
    public static final int NUMBER_OF_TRADE_SLOTS = 7;
    private static final int DEATH_TIME = COTWUtil.secondsToTicks(2.0F);
    private static final EntityDataAccessor<Boolean> DATA_LIGHT_ON = SynchedEntityData.defineId(Beedle.class, EntityDataSerializers.BOOLEAN);

    private SmartBrainSchedule schedule;

    public Beedle(EntityType<? extends Beedle> $$0, Level $$1) {
        super($$0, $$1);
        this.setMaxUpStep(1.0F); // Beedle can step up 1 block without triggering a jump
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Villager.createAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LIGHT_ON, false);
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean hasTrade) {
        return hasTrade ? SoundInit.BEEDLE_URGE.get() : SoundInit.BEEDLE_ANGRY.get();
    }

    @Override
    protected VillagerTrades.ItemListing[] getPotentialTradesForSlot(int tradeSlot) {
        return COTWVillagerTrades.BEEDLE_TRADES.get(tradeSlot);
    }

    @Override
    public VillagerProfession getProfession() {
        return VillagerProfessionInit.BEEDLE.get();
    }

    @Override
    public int getTradeSlots() {
        return NUMBER_OF_TRADE_SLOTS;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (dataAccessor.equals(COTWMob.DATA_WALKING)) {
            this.refreshDimensions();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(COTWAnimations.moveController(this));
        controllers.add(COTWAnimations.poseController(this));
        controllers.add(COTWAnimations.shopController(this));
    }

    public boolean refuseToMove() {
        return this.hasPose(Pose.DYING) || this.hasPose(Pose.SLEEPING);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation navigation = (GroundPathNavigation) super.createNavigation(level);
        navigation.setCanOpenDoors(true);
        return navigation;
    }

    @Override
    protected int getMaxDeathTime() {
        return DEATH_TIME;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isSleeping() ? SoundInit.BEEDLE_SLEEP.get() : super.getAmbientSound();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundInit.BEEDLE_ANGRY.get();
    }

    // AbstractVillager, Villager and WanderingTrader methods


    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        // Doing this here since SBL does not deserialize Brain NBT
        COTWUtil.readBrainFromTag(pCompound, this);
    }

    @Override
    protected void playStartTradingSound() {
        this.playSound(SoundInit.BEEDLE_URGE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public void die(DamageSource pCause) {
        if (this.level() instanceof ServerLevel) {
            Entity killer = pCause.getEntity();

            Player playerKiller = null;
            if (killer instanceof Player) {
                playerKiller = (Player) killer;
            } else if (killer instanceof OwnableEntity ownable) {
                LivingEntity owner = ownable.getOwner();
                if (owner instanceof Player) {
                    playerKiller = (Player) owner;
                }
            }

            if (playerKiller != null) {
                MobEffectInstance activeBadOmen = playerKiller.getEffect(MobEffects.BAD_OMEN);
                int badOmenAmplifier = 2;
                if (activeBadOmen != null) {
                    badOmenAmplifier += activeBadOmen.getAmplifier();
                    playerKiller.removeEffectNoUpdate(MobEffects.BAD_OMEN);
                } else {
                    --badOmenAmplifier;
                }

                badOmenAmplifier = Mth.clamp(badOmenAmplifier, 0, 4);
                MobEffectInstance freshBadOmen = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, badOmenAmplifier, false, false, true);
                if (!this.level().getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                    playerKiller.addEffect(freshBadOmen);
                }
            }
        }
        super.die(pCause);
        this.stopTrading();
    }


    // Brain
    @Override
    public SmartBrain<Beedle> getBrain() {
        return (SmartBrain<Beedle>) super.getBrain();
    }

    @Override
    protected SmartBrainProvider<Beedle> brainProvider() {
        return new SmartBrainProvider<>(this, true, false);
    }

    @Override
    public List<? extends ExtendedSensor<? extends Beedle>> getSensors() {
        return ObjectArrayList.of(
                new COTWNearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new SleepSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends Beedle> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new AdditionalMemories<>(MemoryModuleType.HOME, MemoryModuleType.LAST_SLEPT),
                new COTWVillagerPanicTrigger<Beedle>()
                        .whenPanicking(beedle -> beedle.getBrain().setSchedule(null)),
                new COTWWakeUp<>(),
                new COTWSwim<>(),
                new COTWInteractWithDoor<>(),
                COTWSharedAi.createVanillaStyleLookAtTarget(),
                new MoveToWalkTarget<>(),
                new COTWLookAndFollowTradingPlayerSink<Beedle>(),
                new CustomBehaviour<Beedle>(beedle -> beedle.setLightOn(beedle.level().isNight())));
    }

    @Override
    public BrainActivityGroup<? extends Beedle> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                createIdleLookBehaviors(this),
                createIdleMoveBehaviors(this)
                        .cooldownFor(e -> e.getRandom().nextIntBetweenInclusive(100, 200))
                /*
                new COTWSetLookAndInteract<>()
                        .predicate(le -> le.distanceToSqr(this) <= Mth.square(4) && le.getType().equals(EntityType.PLAYER))
                 */
        );
    }

    private static OneRandomBehaviour<Beedle> createIdleLookBehaviors(Beedle beedle) {
        return new OneRandomBehaviour<>(
                Pair.of(COTWSharedAi.lookAtEntity(beedle, EntityType.PLAYER, 8.0F), 1),
                Pair.of(COTWSharedAi.lookAtEntity(beedle, EntityInit.BEEDLE.get(), 8.0F), 1),
                Pair.of(COTWSharedAi.lookAtAnyEntity(beedle, 8.0F), 1),
                Pair.of(COTWSharedAi.doNothing(), 1)
        );
    }

    /*
    pBrain.addActivity(Activity.IDLE, ImmutableList.of(
    Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
    Pair.of(1, new AnimalMakeLove(EntityType.CAMEL, 1.0F)),
    Pair.of(2, new FollowTemptation((p_250812_) -> {return 2.5F;})),
    Pair.of(3, BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove),
    BabyFollowAdult.create(ADULT_FOLLOW_RANGE, 2.5F))),
    Pair.of(4, new RandomLookAround(UniformInt.of(150, 250), 30.0F, 0.0F, 0.0F)),
    Pair.of(5, new RunOne<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
    ImmutableList.of(
        Pair.of(BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), RandomStroll.stroll(2.0F)), 1),
        Pair.of(BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), SetWalkTargetFromLookTarget.create(2.0F, 3)), 1),
        Pair.of(new CamelAi.RandomSitting(20), 1),
        Pair.of(new DoNothing(30, 60), 1))))));
     */
    private static OneRandomBehaviour<Beedle> createIdleMoveBehaviors(Beedle beedle) {
        return new OneRandomBehaviour<>(
                Pair.of(new SetRandomWalkTarget<>()
                        .speedModifier(0.5F), 2),
                Pair.of(new COTWInteractWith<>(8.0F, 2)
                        .canInteractWith((le, e) -> e.getType().equals(EntityType.PLAYER))
                        .speedModifier(0.5F), 2),
                Pair.of(new COTWInteractWith<>(8.0F, 2)
                        .speedModifier(0.5F), 2),
                Pair.of(new COTWStrollToPoi<>(2, 100)
                        .speedModifier(0.5F), 2),
                Pair.of(new COTWStrollAroundPoi<>(5)
                        .speedModifier(0.5F), 2),
                Pair.of(COTWSharedAi.doNothing(), 10)
        );
    }

    /*
          return ImmutableList.of(getMinimalLookBehavior(),
          Pair.of(5, new RunOne<>(ImmutableList.of(
                Pair.of(workatpoi, 7),
                Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
                Pair.of(StrollToPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
                Pair.of(StrollToPoiList.create(MemoryModuleType.SECONDARY_JOB_SITE, pSpeedModifier, 1, 6, MemoryModuleType.JOB_SITE), 5),
                Pair.of(new HarvestFarmland(), pProfession == VillagerProfession.FARMER ? 2 : 5),
                Pair.of(new UseBonemeal(), pProfession == VillagerProfession.FARMER ? 4 : 7)))),
            Pair.of(10, new ShowTradesToPlayer(400, 1600)),
            Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 4)),
            Pair.of(2, SetWalkTargetFromBlockMemory.create(MemoryModuleType.JOB_SITE, pSpeedModifier, 9, 100, 1200)),
            Pair.of(3, new GiveGiftToHero(100)),
            Pair.of(99, UpdateActivityFromSchedule.create()));
     */

    @Override
    public Map<Activity, BrainActivityGroup<? extends Beedle>> getAdditionalTasks() {
        return Util.make(new Object2ObjectOpenHashMap<>(), map -> {
            map.put(Activity.REST, new BrainActivityGroup<Beedle>(Activity.REST).behaviours(
                    new COTWSetWalkTargetToPoi<>(1, 150).speedModifier(0.5F),
                    new COTWSleepAtHome<Beedle>()
                            .canStart((beedle, home) -> {
                                BlockState stateAtHome = beedle.level().getBlockState(home.pos());
                                return home.pos().closerToCenterThan(beedle.position(), 2.0D)
                                        && (!stateAtHome.is(BlockTags.BEDS) || !stateAtHome.getValue(BedBlock.OCCUPIED));
                            })
                            .canContinue((beedle, home) ->
                                    beedle.getBrain().isActive(Activity.REST)
                                            && home.pos().closerToCenterThan(beedle.position(), 1.14D)),
                    new OneRandomBehaviour<>(
                            //Pair.of(SetClosestHomeAsWalkTarget.create(pSpeedModifier), 1),
                            Pair.of(new COTWInsideBrownianWalk<>().speedModifier(0.5F), 4),
                            //Pair.of(GoToClosestVillage.create(pSpeedModifier, 4), 2),
                            Pair.of(COTWSharedAi.doNothing(20, 40), 2))
                            .startCondition(beedle -> !BrainUtils.hasMemory(beedle, MemoryModuleType.HOME)),
                    getMinimalLookBehavior(this)
                            .startCondition(beedle -> !beedle.isSleeping())
            ));
            map.put(Activity.PANIC, new BrainActivityGroup<Beedle>(Activity.PANIC).behaviours(
                    new COTWVillagerCalmDown<Beedle>()
                            .whenCalmingDown(entity -> {
                                if(entity.getBrain().getSchedule() == null){
                                    entity.getBrain().setSchedule(this.getSchedule());
                                }
                                //entity.getBrain().updateActivityFromSchedule(entity.level().getDayTime(), entity.level().getGameTime());
                            }),
                    //SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, 0.75F, 6, false))
                    COTWSetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, 0.75F, 6, false),
                    //VillageBoundRandomStroll.create(0.75F, 2, 2)),
                    new COTWStrollAroundPoi<>(0).speedModifier(0.75F),
                    getMinimalLookBehavior(this)
            ));
        });
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if(pose == Pose.DYING){
            return EntityInit.BEEDLE_DYING;
        } else if(pose == Pose.SLEEPING || !this.isWalking()){
            return EntityInit.BEEDLE_SITTING;
        }
        return super.getDimensions(pose);
    }

    private static OneRandomBehaviour<Beedle> getMinimalLookBehavior(Beedle beedle) {
        return new OneRandomBehaviour<>(
                Pair.of(COTWSharedAi.lookAtEntity(beedle, EntityType.PLAYER, 8.0F), 2),
                Pair.of(COTWSharedAi.lookAtEntity(beedle, EntityInit.BEEDLE.get(), 8.0F), 2),
                Pair.of(COTWSharedAi.doNothing(), 8));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.tickBrain(this);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.initMemories(pLevel);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    protected void initMemories(ServerLevelAccessor pLevel) {
        COTWSharedAi.setHomeOnSolidGroundBelowSelf(this, pLevel);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public @Nullable SmartBrainSchedule getSchedule() {
        if(this.schedule == null){
            this.schedule = new SmartBrainSchedule()
                    .activityAt(10, Activity.IDLE)
                    .doAt(10, e -> {
                        if(e instanceof Beedle beedle){
                            if(beedle.shouldRestock()){
                                beedle.restock();
                            }
                        }
                    })
                    .activityAt(12000, Activity.REST);
        }
        return this.schedule;
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of();
    }

    public boolean isLightOn() {
        return this.entityData.get(DATA_LIGHT_ON);
    }

    public void setLightOn(boolean lightOn){
        this.entityData.set(DATA_LIGHT_ON, lightOn);
    }

    @Override
    public void startSleeping(BlockPos blockPos) {
        super.startSleeping(blockPos);
        BrainUtils.setMemory(this, MemoryModuleType.LAST_SLEPT, this.level().getGameTime());
        BrainUtils.clearMemory(this, MemoryModuleType.WALK_TARGET);
        BrainUtils.clearMemory(this, MemoryModuleType.LOOK_TARGET);
        BrainUtils.clearMemory(this, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void stopSleeping() {
        super.stopSleeping();
        BrainUtils.setMemory(this, MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return super.getStandingEyeHeight($$0, $$1);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundInit.BEEDLE_SURPRISE.get();
    }

    @Override
    public int getMaxOffersForSlot(int tradeSlot) {
        if(tradeSlot == 1){
            return 2;
        } else {
            return 1;
        }
    }
}
