package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior.*;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWNearbyPlayersSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;

public class Kass extends COTWMob implements Npc, SmartBrainOwner<Kass> {
    private static final EntityDataAccessor<Boolean> DATA_PLAYING_MUSIC = SynchedEntityData.defineId(Kass.class, EntityDataSerializers.BOOLEAN);

    public Kass(EntityType<? extends Kass> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    protected int getMaxDeathTime() {
        return 20;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(COTWAnimations.moveController(this));
        controllers.add(COTWAnimations.musicController(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Villager.createAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PLAYING_MUSIC, false);
    }

    public boolean isPlayingMusic() {
        return this.entityData.get(DATA_PLAYING_MUSIC);
    }

    public void setPlayingMusic(boolean playingMusic){
        this.entityData.set(DATA_PLAYING_MUSIC, playingMusic);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Doing this here since SBL does not deserialize Brain NBT
        COTWUtil.readBrainFromTag(tag, this);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.initMemories();
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    protected void initMemories() {
        GlobalPos currentGlobalPos = GlobalPos.of(this.level().dimension(), this.blockPosition());
        BrainUtils.setMemory(this, MemoryModuleType.HOME, currentGlobalPos);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.tickBrain(this);
    }

    @Override
    protected SmartBrainProvider<Kass> brainProvider() {
        return new SmartBrainProvider<>(this, true, false);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends Kass>> getSensors() {
        return ObjectArrayList.of(
                new COTWNearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends Kass> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new AdditionalMemories<>(MemoryModuleType.HOME),
                new COTWSwim<>(),
                new COTWInteractWithDoor<>(),
                COTWSharedAi.createVanillaStyleLookAtTarget(),
                new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends Kass> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                createIdleLookBehaviors(this),
                createIdleMoveBehaviors()
        );
    }

    private static OneRandomBehaviour<Kass> createIdleLookBehaviors(Kass kass) {
        return new OneRandomBehaviour<>(
                Pair.of(COTWSharedAi.lookAtEntity(kass, EntityType.PLAYER, 8.0F), 1),
                Pair.of(COTWSharedAi.lookAtEntity(kass, EntityInit.KASS.get(), 8.0F), 1),
                Pair.of(COTWSharedAi.lookAtAnyEntity(kass, 8.0F), 1),
                Pair.of(COTWSharedAi.doNothing(), 1)
        );
    }
    private static OneRandomBehaviour<Kass> createIdleMoveBehaviors() {
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
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(Activity.IDLE);
    }
}
