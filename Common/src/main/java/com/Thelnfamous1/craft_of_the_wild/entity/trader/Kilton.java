package com.Thelnfamous1.craft_of_the_wild.entity.trader;

import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior.*;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWNearbyPlayersSensor;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;
import java.util.Map;

public class Kilton extends COTWTrader implements SmartBrainOwner<Kilton> {
    public static final int NUMBER_OF_TRADE_SLOTS = 9;
    private static final int DEATH_TIME = COTWUtil.secondsToTicks(1.7857F);

    private SmartBrainSchedule schedule;

    public Kilton(EntityType<? extends Kilton> $$0, Level $$1) {
        super($$0, $$1);
        this.setMaxUpStep(1.0F); // Kilton can step up 1 block without triggering a jump
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Villager.createAttributes();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(COTWAnimations.moveController(this));
        controllers.add(COTWAnimations.poseController(this));
    }

    public boolean refuseToMove(boolean checkAttacking) {
        return this.hasPose(Pose.DYING);
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
        return SoundInit.KILTON_AGREE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundInit.KILTON_DISAPPOINTED.get();
    }

    // AbstractVillager, Villager and WanderingTrader methods

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean hasTrade) {
        return hasTrade ? SoundInit.KILTON_AGREE.get() : SoundInit.KILTON_DISAPPOINTED.get();
    }

    @Override
    protected VillagerTrades.ItemListing[] getPotentialTradesForSlot(int tradeSlot) {
        return COTWVillagerTrades.KILTON_TRADES.get(tradeSlot);
    }

    @Override
    public VillagerProfession getProfession() {
        return VillagerProfessionInit.KILTON.get();
    }

    @Override
    public int getTradeSlots() {
        return NUMBER_OF_TRADE_SLOTS;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        // Doing this here since SBL does not deserialize Brain NBT
        COTWUtil.readBrainFromTag(pCompound, this);
    }

    @Override
    protected void playStartTradingSound() {
        this.playSound(SoundInit.KILTON_AGREE.get(), this.getSoundVolume(), this.getVoicePitch());
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
                MobEffectInstance nausea = new MobEffectInstance(MobEffects.CONFUSION, COTWUtil.secondsToTicks(4), 2, false, false, true);
                MobEffectInstance poison = new MobEffectInstance(MobEffects.POISON, COTWUtil.secondsToTicks(4), 2, false, false, true);
                playerKiller.addEffect(nausea);
                playerKiller.addEffect(poison);
            }
        }
        super.die(pCause);
        this.stopTrading();
    }


    // Brain
    @Override
    public SmartBrain<Kilton> getBrain() {
        return (SmartBrain<Kilton>) super.getBrain();
    }

    @Override
    protected SmartBrainProvider<Kilton> brainProvider() {
        return new SmartBrainProvider<>(this, true, false);
    }

    @Override
    public List<? extends ExtendedSensor<? extends Kilton>> getSensors() {
        return ObjectArrayList.of(
                new COTWNearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends Kilton> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new AdditionalMemories<>(MemoryModuleType.HOME),
                new COTWVillagerPanicTrigger<Kilton>()
                        .whenPanicking(kilton -> kilton.getBrain().setSchedule(null)),
                new COTWSwim<>(),
                new COTWInteractWithDoor<>(),
                COTWSharedAi.createVanillaStyleLookAtTarget(),
                new MoveToWalkTarget<>(),
                new COTWLookAndFollowTradingPlayerSink<Kilton>());
    }

    @Override
    public BrainActivityGroup<? extends Kilton> getIdleTasks() {
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

    private static OneRandomBehaviour<Kilton> createIdleLookBehaviors(Kilton kilton) {
        return new OneRandomBehaviour<>(
                Pair.of(COTWSharedAi.lookAtEntity(kilton, EntityType.PLAYER, 8.0F), 1),
                Pair.of(COTWSharedAi.lookAtEntity(kilton, EntityInit.KILTON.get(), 8.0F), 1),
                Pair.of(COTWSharedAi.lookAtAnyEntity(kilton, 8.0F), 1),
                Pair.of(COTWSharedAi.doNothing(), 1)
        );
    }

    private static OneRandomBehaviour<Kilton> createIdleMoveBehaviors(Kilton kilton) {
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
    @Override
    public Map<Activity, BrainActivityGroup<? extends Kilton>> getAdditionalTasks() {
        return Util.make(new Object2ObjectOpenHashMap<>(), map -> {
            map.put(Activity.PANIC, new BrainActivityGroup<Kilton>(Activity.PANIC).behaviours(
                    new COTWVillagerCalmDown<Kilton>()
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
            return EntityInit.KILTON_DYING;
        }
        return super.getDimensions(pose);
    }

    private static OneRandomBehaviour<Kilton> getMinimalLookBehavior(Kilton kilton) {
        return new OneRandomBehaviour<>(
                Pair.of(COTWSharedAi.lookAtEntity(kilton, EntityType.PLAYER, 8.0F), 2),
                Pair.of(COTWSharedAi.lookAtEntity(kilton, EntityInit.KILTON.get(), 8.0F), 2),
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
                        if(e instanceof Kilton kilton){
                            if(kilton.shouldRestock()){
                                kilton.restock();
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
        return SoundInit.KILTON_FASCINATED.get();
    }
}
