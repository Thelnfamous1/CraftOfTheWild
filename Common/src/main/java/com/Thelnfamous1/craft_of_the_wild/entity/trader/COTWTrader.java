package com.Thelnfamous1.craft_of_the_wild.entity.trader;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.compat.customvillagertrades.CustomVillagerTradesCompat;
import com.Thelnfamous1.craft_of_the_wild.entity.COTWMob;
import com.Thelnfamous1.craft_of_the_wild.entity.COTWVillager;
import com.Thelnfamous1.craft_of_the_wild.init.CriterionInit;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class COTWTrader extends COTWMob implements Npc, COTWVillager {
    public static final long RESTOCK_INTERVAL = 12000L;
    public static final long DAY_LENGTH = 24000L;
    public static final int MAX_RESTOCKS = 2;
    public static final int DEFAULT_MAX_OFFERS_PER_SLOT = 1;
    protected static final EntityDataAccessor<Boolean> DATA_TRADING = SynchedEntityData.defineId(COTWTrader.class, EntityDataSerializers.BOOLEAN);
    @Nullable
    protected MerchantOffers offers;
    protected long lastRestockGameTime;
    protected int numberOfRestocksToday;
    @Nullable
    private Player tradingPlayer;
    private long lastRestockCheckDayTime;
    private int notifyTradeSoundCooldown;

    public COTWTrader(EntityType<? extends COTWMob> $$0, Level $$1) {
        super($$0, $$1);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRADING, false);
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        boolean shouldStop = this.tradingPlayer != null && player == null;
        this.tradingPlayer = player;
        if (!this.level().isClientSide) {
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
            CriterionInit.TRADE.trigger(serverTradingPlayer, this, merchantOffer.getResult());
        }
        if (!this.level().isClientSide && this.notifyTradeSoundCooldown == 0) {
            this.playNotifyTradeSound();
            this.notifyTradeSoundCooldown = 20;
        }
    }

    protected void playNotifyTradeSound() {
        this.playSound(this.getNotifyTradeSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if(this.notifyTradeSoundCooldown > 0){
            this.notifyTradeSoundCooldown--;
        }
    }

    // This technically gets called before notifyTrade when taking an item out of the trade result slot
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
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    public void addOffersFromItemListings(MerchantOffers merchantOffers, VillagerTrades.ItemListing[] potentialTrades, int maxOffers) {
        COTWVillager.addOffersFromitemListings(this, merchantOffers, potentialTrades, maxOffers);

    }

    public boolean isTrading() {
        if (this.level().isClientSide) {
            return this.entityData.get(DATA_TRADING);
        }
        return this.tradingPlayer != null;
    }

    protected void stopTrading() {
        this.setTradingPlayer(null);
    }

    protected abstract SoundEvent getTradeUpdatedSound(boolean hasTrade);

    protected void updateTrades() {
        boolean useCustomVillagerTrades = Services.PLATFORM.isModLoaded(Constants.CUSTOM_VILLAGER_TRADES_MODID);
        int tradeSlots = this.getTradeSlots();

        MerchantOffers merchantOffers = this.getOffers();
        for (int tradeSlot = 1; tradeSlot <= tradeSlots; tradeSlot++) {
            int maxOffersForSlot = this.getMaxOffersForSlot(tradeSlot);
            /*
            if (useCustomVillagerTrades) {
                maxOffersForSlot = CustomVillagerTradesCompat.getMaxOffers(this).orElse(maxOffersForSlot);
            }
             */
            VillagerTrades.ItemListing[] potentialTradesForSlot = getPotentialTradesForSlot(tradeSlot);
            if (useCustomVillagerTrades) {
                potentialTradesForSlot = CustomVillagerTradesCompat.replaceItemListings(this, potentialTradesForSlot, tradeSlot);
            }
            if (potentialTradesForSlot != null) {
                this.addOffersFromItemListings(merchantOffers, potentialTradesForSlot, maxOffersForSlot);
            }
        }
    }

    protected abstract VillagerTrades.ItemListing[] getPotentialTradesForSlot(int tradeSlot);

    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int $$1 = 3 + this.random.nextInt(4);
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), $$1));
        }

    }

    // Restock
    public void restock() {
        if (Constants.DEBUG_BEEDLE_RESTOCK)
            Constants.LOG.info("{} is restocking their trades!", this);
        this.updateDemand();

        for (MerchantOffer merchantoffer : this.getOffers()) {
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
        for (MerchantOffer merchantoffer : this.getOffers()) {
            if (merchantoffer.needsRestock()) {
                return true;
            }
        }

        return false;
    }

    private boolean allowedToRestock() {
        return this.numberOfRestocksToday == 0 ||
                this.numberOfRestocksToday < 2
                        && this.level().getGameTime() > this.lastRestockGameTime + DAY_LENGTH;
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
            for (MerchantOffer merchantoffer : this.getOffers()) {
                merchantoffer.resetUses();
            }
        }

        for (int j = 0; j < restocksRemaining; ++j) {
            this.updateDemand();
        }

        this.resendOffersToTradingPlayer();
    }

    private void updateDemand() {
        for (MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.updateDemand();
        }

    }

    private void resetNumberOfRestocks() {
        this.catchUpDemand();
        this.numberOfRestocksToday = 0;
    }

    private boolean isCloseEnoughToTradingPlayer() {
        Player tradingPlayer = this.getTradingPlayer();
        return tradingPlayer != null && this.closerThan(tradingPlayer, ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE);
    }

    @Override
    public abstract VillagerProfession getProfession();

    public abstract int getTradeSlots();

    public int getMaxOffersForSlot(int tradeSlot) {
        return DEFAULT_MAX_OFFERS_PER_SLOT;
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
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        ItemStack spawnEgg = this.getPickResult();
        if ((spawnEgg == null || !spawnEgg.is(itemstack.getItem())) && this.isAlive() && !this.isTrading() && !this.isBaby()) {
            if (pHand == InteractionHand.MAIN_HAND) {
                pPlayer.awardStat(Stats.TALKED_TO_VILLAGER);
            }

            if (!this.getOffers().isEmpty()) {
                if (!this.level().isClientSide) {
                    this.setTradingPlayer(pPlayer);
                    this.openTradingScreen(pPlayer, this.getDisplayName(), 1);
                    this.ambientSoundTime = -this.getAmbientSoundInterval();
                    playStartTradingSound();
                }

            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    protected abstract void playStartTradingSound();

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel level) {
        this.stopTrading();
        return super.changeDimension(level);
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }
}
