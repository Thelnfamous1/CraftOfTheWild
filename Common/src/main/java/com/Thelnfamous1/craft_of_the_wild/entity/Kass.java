package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior.*;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWNearbyPlayersSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Kass extends COTWMob implements Npc, SmartBrainOwner<Kass>, CustomMusicPlayer, InventoryCarrier, ContainerListener {
    private static final EntityDataAccessor<Boolean> DATA_PLAYING_MUSIC = SynchedEntityData.defineId(Kass.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> DATA_MUSIC_DISC = SynchedEntityData.defineId(Kass.class, EntityDataSerializers.ITEM_STACK);
    public static final String PLAYING_MUSIC_TAG_KEY = "PlayingMusic";
    private static final Map<RecordItem, CustomMusicData> KASS_MUSIC = Maps.newHashMap();
    private final SimpleContainer inventory = new SimpleContainer(1);
    private int ticksSinceLastEvent;

    public Kass(EntityType<? extends Kass> $$0, Level $$1) {
        super($$0, $$1);
        this.getInventory().addListener(this);
        this.updateContainerEquipment();
    }

    @Override
    protected int getMaxDeathTime() {
        return 20;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(COTWAnimations.musicController(this));
        controllers.add(COTWAnimations.moveController(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Villager.createAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PLAYING_MUSIC, true);
        this.entityData.define(DATA_MUSIC_DISC, ItemStack.EMPTY);
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
        this.writeInventoryToTag(tag);
        tag.putBoolean(PLAYING_MUSIC_TAG_KEY, this.isPlayingMusic());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Doing this here since SBL does not deserialize Brain NBT
        COTWUtil.readBrainFromTag(tag, this);
        this.readInventoryFromTag(tag);
        this.updateContainerEquipment();
        if(tag.contains(PLAYING_MUSIC_TAG_KEY, Tag.TAG_ANY_NUMERIC)){
            this.setPlayingMusic(tag.getBoolean(PLAYING_MUSIC_TAG_KEY));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.initMemories($$0);
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    protected void initMemories(ServerLevelAccessor pLevel) {
        COTWSharedAi.setHomeOnSolidGroundBelowSelf(this, pLevel);
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
                        .startCondition(Predicate.not(Kass::isPlayingMusic))
                        .stopIf(Kass::isPlayingMusic)
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

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(player.isSecondaryUseActive()){
            if(!this.level().isClientSide){
                this.setPlayingMusic(!this.isPlayingMusic());
                if(this.isPlayingMusic()){
                    COTWUtil.stopWalking(this);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack storedMusicDisc = this.getInventory().getItem(0);
        if (storedMusicDisc.isEmpty() && !itemInHand.isEmpty() && itemInHand.is(ItemTags.MUSIC_DISCS)) {
            ItemStack musicDiscToStore = itemInHand.copyWithCount(1);
            this.getInventory().addItem(musicDiscToStore);
            this.removeInteractionItem(player, itemInHand);
            //this.level().playSound(player, this, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (!storedMusicDisc.isEmpty() && hand == InteractionHand.MAIN_HAND && itemInHand.isEmpty()) {
            //this.level().playSound(player, this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.swing(InteractionHand.MAIN_HAND);
            for (ItemStack inventoryStack : this.getInventory().removeAllItems()) {
                if(!player.addItem(inventoryStack)){
                    BehaviorUtils.throwItem(this, inventoryStack, this.position());
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    private void removeInteractionItem(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    public void setMusicDisc(ItemStack musicDisc){
        this.entityData.set(DATA_MUSIC_DISC, musicDisc);
    }

    public ItemStack getMusicDisc(){
        return this.entityData.get(DATA_MUSIC_DISC);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.ticksSinceLastEvent;
        if (this.isPlayingMusic()) {
            if (this.shouldSendJukeboxPlayingEvent()) {
                this.ticksSinceLastEvent = 0;
                this.gameEvent(GameEvent.JUKEBOX_PLAY);
                this.spawnMusicParticles();
            }
        }
        if (!this.level().isClientSide) {
            if (this.canPlayCustomMusic()) {
                this.level().broadcastEntityEvent(this, CustomMusicPlayer.MUSIC_PLAY_ID);
            }
            else {
                this.level().broadcastEntityEvent(this, CustomMusicPlayer.MUSIC_STOP_ID);
            }
        }
    }

    private boolean shouldSendJukeboxPlayingEvent() {
        return this.ticksSinceLastEvent >= 20;
    }

    private void spawnMusicParticles() {
        if (this.level() instanceof ServerLevel serverlevel) {
            Vec3 position = this.getEyePosition()
                    .add(new Vec3(0, 0, 1)
                            .yRot(-this.yBodyRot * Mth.DEG_TO_RAD));
            float color = serverlevel.getRandom().nextInt(4) / 24.0F;
            Vec3 offset = new Vec3(color, 0, 0);
            serverlevel.sendParticles(ParticleTypes.NOTE,
                    position.x(), position.y(), position.z(),
                    0,
                    offset.x(), offset.y(), offset.z(),
                    1.0D);
        }

    }

    @Override
    public void handleEntityEvent(byte eventId) {
        CustomMusicPlayer.handleCustomMusicEvent(this, eventId);
        super.handleEntityEvent(eventId);
    }

    @Override
    public CustomMusicData getCustomMusic() {
        Item item = this.getMusicDisc().getItem();
        if(item instanceof RecordItem recordItem){
            return KASS_MUSIC.computeIfAbsent(recordItem,
                    k -> CustomMusicData.forRecord(recordItem.getSound(), Vec3.ZERO, true));
        }
        return KASS_MUSIC.computeIfAbsent(null, k -> CustomMusicData.forRecord(SoundInit.KASS_THEME.get(), Vec3.ZERO, true));
    }

    @Override
    public boolean canPlayCustomMusic() {
        return !this.isSilent() && this.isPlayingMusic();
    }

    @Override
    public boolean canCustomMusicBeHeardBy(Player player) {
        return this.distanceToSqr(player) <= Mth.square(64);
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
    }

    @Override
    public void containerChanged(Container container) {
        this.updateContainerEquipment();
    }

    private void updateContainerEquipment() {
        if (!this.level().isClientSide) {
            this.setMusicDisc(this.getInventory().getItem(0));
        }
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return false;
    }
}
