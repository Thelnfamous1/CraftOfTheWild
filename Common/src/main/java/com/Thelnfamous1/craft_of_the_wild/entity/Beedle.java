package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior.*;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWNearbyPlayersSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.SleepSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.entity.trades.COTWVillagerTrades;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
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
import java.util.Set;

public class Beedle extends COTWMob implements Npc, Merchant, SmartBrainOwner<Beedle> {
    private static final int DEATH_TIME = COTWUtil.secondsToTicks(2.0F);
    public static final int NUMBER_OF_TRADE_SLOTS = 7;
    private static final EntityDataAccessor<Boolean> DATA_TRADING = SynchedEntityData.defineId(Beedle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_LIGHT_ON = SynchedEntityData.defineId(Beedle.class, EntityDataSerializers.BOOLEAN);
    public static final long RESTOCK_INTERVAL = 12000L;
    public static final long DAY_LENGTH = 24000L;
    public static final int MAX_RESTOCKS = 2;
    @Nullable
    private Player tradingPlayer;
    @Nullable
    protected MerchantOffers offers;

    private long lastRestockGameTime;
    private int numberOfRestocksToday;
    private long lastRestockCheckDayTime;
    private SmartBrainSchedule schedule;

    public Beedle(EntityType<? extends Beedle> $$0, Level $$1) {
        super($$0, $$1);
        this.setMaxUpStep(1.0F); // Beedle can step up 1 block without triggering a jump
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Villager.createAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRADING, false);
        this.entityData.define(DATA_LIGHT_ON, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if(dataAccessor.equals(COTWMob.DATA_WALKING)){
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
        return this.hasPose(Pose.DYING) || this.hasPose(Pose.SLEEPING) || this.isTrading();
    }

    @Override
    protected int getMaxDeathTime() {
        return DEATH_TIME;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        boolean shouldStop = this.tradingPlayer != null && player == null;
        this.tradingPlayer = player;
        if(!this.level().isClientSide){
            this.entityData.set(DATA_TRADING, this.tradingPlayer != null);
        }
        if (shouldStop) {
            this.stopTrading();
        }
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.updateTrades();
        }

        return this.offers;
    }

    @Override
    public void overrideOffers(MerchantOffers merchantOffers) {

    }

    @Override
    public void notifyTrade(MerchantOffer merchantOffer) {
        merchantOffer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(merchantOffer);
        Player tradingPlayer = this.getTradingPlayer();
        if (tradingPlayer instanceof ServerPlayer serverTradingPlayer) {
            //CriteriaTriggers.TRADE.trigger(serverTradingPlayer, this, merchantOffer.getResult());
        }
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.playSound(this.getNotifyTradeSound(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack result) {
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.playSound(this.getTradeUpdatedSound(!result.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int i) {

    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundInit.BEEDLE_SURPRISE.get();
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
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

    protected void addOffersFromItemListings(MerchantOffers merchantOffers, VillagerTrades.ItemListing[] potentialTrades, int maxOffers) {
        Set<Integer> indices = Sets.newHashSet();
        if (potentialTrades.length > maxOffers) {
            while(indices.size() < maxOffers) {
                indices.add(this.random.nextInt(potentialTrades.length));
            }
        } else {
            for(int i = 0; i < potentialTrades.length; ++i) {
                indices.add(i);
            }
        }

        for(Integer index : indices) {
            VillagerTrades.ItemListing trade = potentialTrades[index];
            MerchantOffer offer = trade.getOffer(this, this.random);
            if (offer != null) {
                merchantOffers.add(offer);
            }
        }

    }

    public boolean isTrading() {
        if(this.level().isClientSide){
            return this.entityData.get(DATA_TRADING);
        }
        return this.tradingPlayer != null;
    }

    protected void stopTrading() {
        this.setTradingPlayer(null);
    }

    protected SoundEvent getTradeUpdatedSound(boolean hasTrade) {
        return hasTrade ? SoundInit.BEEDLE_URGE.get() : SoundInit.BEEDLE_ANGRY.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        MerchantOffers merchantoffers = this.getOffers();
        if (!merchantoffers.isEmpty()) {
            pCompound.put("Offers", merchantoffers.createTag());
        }
        pCompound.putLong("LastRestock", this.lastRestockGameTime);
        pCompound.putInt("RestocksToday", this.numberOfRestocksToday);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Offers", Tag.TAG_COMPOUND)) {
            this.offers = new MerchantOffers(pCompound.getCompound("Offers"));
        }
        this.lastRestockGameTime = pCompound.getLong("LastRestock");
        this.setCanPickUpLoot(true);

        this.numberOfRestocksToday = pCompound.getInt("RestocksToday");
        // Doing this here since SBL does not deserialize Brain NBT
        COTWUtil.readBrainFromTag(pCompound, this);
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel level) {
        this.stopTrading();
        return super.changeDimension(level);
    }

    @Override
    public void die(DamageSource pCause) {
        if (this.level() instanceof ServerLevel) {
            Entity killer = pCause.getEntity();

            Player playerKiller = null;
            if (killer instanceof Player) {
                playerKiller = (Player)killer;
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

    protected void updateTrades() {
        for(int tradeSlot = 1; tradeSlot <= NUMBER_OF_TRADE_SLOTS; tradeSlot++){
            VillagerTrades.ItemListing[] potentialTradesForSlot = COTWVillagerTrades.BEEDLE_TRADES.get(tradeSlot);
            if (potentialTradesForSlot != null) {
                MerchantOffers offers = this.getOffers();
                this.addOffersFromItemListings(offers, potentialTradesForSlot, 1);
            }
        }

    }

    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int $$1 = 3 + this.random.nextInt(4);
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), $$1));
        }

    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!itemstack.is(ItemInit.BEEDLE_SPAWN_EGG.get()) && this.isAlive() && !this.isTrading() && !this.isBaby()) {
            if (pHand == InteractionHand.MAIN_HAND) {
                pPlayer.awardStat(Stats.TALKED_TO_VILLAGER);
            }

            if (!this.getOffers().isEmpty()) {
                if (!this.level().isClientSide) {
                    this.setTradingPlayer(pPlayer);
                    this.openTradingScreen(pPlayer, this.getDisplayName(), 1);
                    this.ambientSoundTime = -this.getAmbientSoundInterval();
                    this.playSound(SoundInit.BEEDLE_URGE.get(), this.getSoundVolume(), this.getVoicePitch());
                }

            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    // Restock
    public void restock() {
        this.updateDemand();

        for(MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.resetUses();
        }

        this.resendOffersToTradingPlayer();
        this.lastRestockGameTime = this.level().getGameTime();
        ++this.numberOfRestocksToday;
    }

    private void resendOffersToTradingPlayer() {
        MerchantOffers merchantoffers = this.getOffers();
        Player player = this.getTradingPlayer();
        if (player != null && !merchantoffers.isEmpty()) {
            player.sendMerchantOffers(player.containerMenu.containerId, merchantoffers, 1, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
        }

    }

    private boolean needsToRestock() {
        for(MerchantOffer merchantoffer : this.getOffers()) {
            if (merchantoffer.needsRestock()) {
                return true;
            }
        }

        return false;
    }

    private boolean allowedToRestock() {
        return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level().getGameTime() > this.lastRestockGameTime + 2400L;
    }

    public boolean shouldRestock() {
        long nextRestockTime = this.lastRestockGameTime + RESTOCK_INTERVAL;
        long currentTime = this.level().getGameTime();
        boolean shouldRestock = currentTime > nextRestockTime;
        long currentDayTime = this.level().getDayTime();
        if (this.lastRestockCheckDayTime > 0L) {
            long lastDayProgress = this.lastRestockCheckDayTime / DAY_LENGTH;
            long currentDayProgress = currentDayTime / DAY_LENGTH;
            shouldRestock |= currentDayProgress > lastDayProgress;
        }

        this.lastRestockCheckDayTime = currentDayTime;
        if (shouldRestock) {
            this.lastRestockGameTime = currentTime;
            this.resetNumberOfRestocks();
        }

        return this.allowedToRestock() && this.needsToRestock();
    }

    private void catchUpDemand() {
        int restocksRemaining = MAX_RESTOCKS - this.numberOfRestocksToday;
        if (restocksRemaining > 0) {
            for(MerchantOffer merchantoffer : this.getOffers()) {
                merchantoffer.resetUses();
            }
        }

        for(int j = 0; j < restocksRemaining; ++j) {
            this.updateDemand();
        }

        this.resendOffersToTradingPlayer();
    }

    private void updateDemand() {
        for(MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.updateDemand();
        }

    }

    private void resetNumberOfRestocks() {
        this.catchUpDemand();
        this.numberOfRestocksToday = 0;
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
                new COTWLookAndFollowTradingPlayerSink<>(),
                new CustomBehaviour<Beedle>(beedle -> beedle.setLightOn(beedle.level().isNight())));
    }

    @Override
    public BrainActivityGroup<? extends Beedle> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                createIdleLookBehaviors(this),
                createIdleMoveBehaviors(this),
                new COTWSetLookAndInteract<>()
                        .predicate(le -> le.distanceToSqr(this) <= Mth.square(4) && le.getType().equals(EntityType.PLAYER))
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
                Pair.of(COTWSharedAi.doNothing(), 1)
        );
    }

    @Override
    public Map<Activity, BrainActivityGroup<? extends Beedle>> getAdditionalTasks() {
        return Util.make(new Object2ObjectOpenHashMap<>(), map -> {
            map.put(Activity.REST, new BrainActivityGroup<Beedle>(Activity.REST).behaviours(
                    new COTWSetWalkTargetToPoi<>(1, 150).speedModifier(0.5F),
                    new COTWSleepAtHome<>()
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
        this.initMemories();
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    protected void initMemories() {
        GlobalPos currentGlobalPos = GlobalPos.of(this.level().dimension(), this.blockPosition());
        BrainUtils.setMemory(this, MemoryModuleType.HOME, currentGlobalPos);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public @Nullable SmartBrainSchedule getSchedule() {
        if(this.schedule == null){
            this.schedule = new SmartBrainSchedule().activityAt(10, Activity.IDLE).activityAt(12000, Activity.REST);
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
    public boolean canBeLeashed(Player player) {
        return false;
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
}
