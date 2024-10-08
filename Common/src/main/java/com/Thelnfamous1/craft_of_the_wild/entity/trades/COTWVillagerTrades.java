package com.Thelnfamous1.craft_of_the_wild.entity.trades;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class COTWVillagerTrades {
    public static final Int2ObjectMap<VillagerTrades.ItemListing[]> BEEDLE_TRADES = toIntMap(
            ImmutableMap.of(
                    1, new VillagerTrades.ItemListing[]{
                            new TippedArrowForItemsAndEmeralds(Items.ARROW, 5, Items.TIPPED_ARROW, 5, 2, 12, 1)
                    },
                    2, new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(Items.ARROW, 1, 16, 12, 1),
                            new ItemsForEmeralds(Items.ARROW, 1, 5, 12, 1)
                    },
                    3, new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(Items.PAINTING, 2, 3, 12, 1),
                            new ItemsForEmeralds(Items.NAME_TAG, 20, 1, 16, 1)
                    },
                    4, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.COAL, 15, 1, 12, 1),
                            new EmeraldForItems(Items.INK_SAC, 5, 1, 12, 1)
                    },
                    5, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.ROTTEN_FLESH, 32, 1, 16, 1)
                    },
                    6, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.LEATHER, 6, 1, 12, 1),
                            new EmeraldForItems(Items.RABBIT_HIDE, 2, 4, 16, 1)
                    },
                    7, new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(Items.BEEF, 1, 10, 12, 1),
                            new ItemsForEmeralds(Items.MUTTON, 1, 7, 12, 1),
                            new ItemsForEmeralds(Items.BREAD, 1, 6, 16, 1)
                    }));

    private static Int2ObjectMap<VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> pMap) {
        return new Int2ObjectOpenHashMap<>(pMap);
    }

    static class EmeraldForItems implements VillagerTrades.ItemListing {
        private final Item item;
        private final int cost;
        private final int emeraldCount;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public EmeraldForItems(ItemLike pItem, int pCost, int pMaxUses, int pVillagerXp) {
            this(pItem, pCost, 1, pMaxUses, pVillagerXp);
        }

        public EmeraldForItems(ItemLike pItem, int pCost, int pEmeraldCount, int pMaxUses, int pVillagerXp) {
            this.item = pItem.asItem();
            this.cost = pCost;
            this.emeraldCount = pEmeraldCount;
            this.maxUses = pMaxUses;
            this.villagerXp = pVillagerXp;
            this.priceMultiplier = 0.05F;
        }

        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            ItemStack itemstack = new ItemStack(this.item, this.cost);
            return new MerchantOffer(itemstack, new ItemStack(Items.EMERALD, this.emeraldCount), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class ItemsAndEmeraldsToItems implements VillagerTrades.ItemListing {
        private final ItemStack fromItem;
        private final int fromCount;
        private final int emeraldCost;
        private final ItemStack toItem;
        private final int toCount;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsAndEmeraldsToItems(ItemLike pFromItem, int pFromCount, Item pToItem, int pToCount, int pMaxUses, int pVillagerXp) {
            this(pFromItem, pFromCount, 1, pToItem, pToCount, pMaxUses, pVillagerXp);
        }

        public ItemsAndEmeraldsToItems(ItemLike pFromItem, int pFromCount, int pEmeraldCost, Item pToItem, int pToCount, int pMaxUses, int pVillagerXp) {
            this.fromItem = new ItemStack(pFromItem);
            this.fromCount = pFromCount;
            this.emeraldCost = pEmeraldCost;
            this.toItem = new ItemStack(pToItem);
            this.toCount = pToCount;
            this.maxUses = pMaxUses;
            this.villagerXp = pVillagerXp;
            this.priceMultiplier = 0.05F;
        }

        @Nullable
        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.fromItem.getItem(), this.fromCount), new ItemStack(this.toItem.getItem(), this.toCount), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class ItemsForEmeralds implements VillagerTrades.ItemListing {
        private final ItemStack itemStack;
        private final int emeraldCost;
        private final int numberOfItems;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForEmeralds(Block pBlock, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp) {
            this(new ItemStack(pBlock), pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp);
        }

        public ItemsForEmeralds(Item pItem, int pEmeraldCost, int pNumberOfItems, int pVillagerXp) {
            this(new ItemStack(pItem), pEmeraldCost, pNumberOfItems, 12, pVillagerXp);
        }

        public ItemsForEmeralds(Item pItem, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp) {
            this(new ItemStack(pItem), pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp);
        }

        public ItemsForEmeralds(ItemStack pItemStack, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp) {
            this(pItemStack, pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp, 0.05F);
        }

        public ItemsForEmeralds(ItemStack pItemStack, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp, float pPriceMultiplier) {
            this.itemStack = pItemStack;
            this.emeraldCost = pEmeraldCost;
            this.numberOfItems = pNumberOfItems;
            this.maxUses = pMaxUses;
            this.villagerXp = pVillagerXp;
            this.priceMultiplier = pPriceMultiplier;
        }

        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class TippedArrowForItemsAndEmeralds implements VillagerTrades.ItemListing {
        /** An ItemStack that can have potion effects written to it. */
        private final ItemStack toItem;
        private final int toCount;
        private final int emeraldCost;
        private final int maxUses;
        private final int villagerXp;
        private final Item fromItem;
        private final int fromCount;
        private final float priceMultiplier;

        public TippedArrowForItemsAndEmeralds(Item pFromItem, int pFromCount, Item pToItem, int pToCount, int pEmeraldCost, int pMaxUses, int pVillagerXp) {
            this.toItem = new ItemStack(pToItem);
            this.emeraldCost = pEmeraldCost;
            this.maxUses = pMaxUses;
            this.villagerXp = pVillagerXp;
            this.fromItem = pFromItem;
            this.fromCount = pFromCount;
            this.toCount = pToCount;
            this.priceMultiplier = 0.05F;
        }

        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            ItemStack itemstack = new ItemStack(Items.EMERALD, this.emeraldCost);
            List<Potion> list = BuiltInRegistries.POTION.stream().filter((p_35804_) -> {
                return !p_35804_.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(p_35804_);
            }).collect(Collectors.toList());
            Potion potion = list.get(pRandom.nextInt(list.size()));
            ItemStack itemstack1 = PotionUtils.setPotion(new ItemStack(this.toItem.getItem(), this.toCount), potion);
            return new MerchantOffer(itemstack, new ItemStack(this.fromItem, this.fromCount), itemstack1, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }
}
