package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.entity.animation.COTWAnimations;
import com.Thelnfamous1.craft_of_the_wild.entity.trades.COTWVillagerTrades;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Set;

public class Beedle extends COTWMob implements Npc, Merchant {
    private static final int DEATH_TIME = COTWUtil.secondsToTicks(2.0F);
    public static final int NUMBER_OF_TRADE_SLOTS = 7;
    private static final EntityDataAccessor<Boolean> DATA_TRADING = SynchedEntityData.defineId(Beedle.class, EntityDataSerializers.BOOLEAN);
    @Nullable
    private Player tradingPlayer;
    @Nullable
    protected MerchantOffers offers;

    public Beedle(EntityType<? extends Beedle> $$0, Level $$1) {
        super($$0, $$1);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Villager.createAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRADING, false);
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Offers", Tag.TAG_COMPOUND)) {
            this.offers = new MerchantOffers(pCompound.getCompound("Offers"));
        }
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel level) {
        this.stopTrading();
        return super.changeDimension(level);
    }

    @Override
    public void die(DamageSource pCause) {
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
}
