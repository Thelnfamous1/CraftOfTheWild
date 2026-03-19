package com.Thelnfamous1.craft_of_the_wild.entity.trader.trades;

import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
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

public class COTWVillagerTrades {
    public static final Int2ObjectMap<VillagerTrades.ItemListing[]> BEEDLE_TRADES = toIntMap(
            ImmutableMap.of(
                    1, new VillagerTrades.ItemListing[]{
                            new TippedArrowForItemsAndEmeralds(Items.ARROW, 5, Items.TIPPED_ARROW, 5, 2, 12, 1),
                            new EmeraldForItems(ItemInit.LUMINOUS_STONE.get(), 2, 12, 12, 1)
                    },
                    2, new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(Items.ARROW, 1, 16, Integer.MAX_VALUE, 1),
                            new ItemsForEmeralds(Items.ARROW, 1, 5, Integer.MAX_VALUE, 1)
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
                            new EmeraldForItems(Items.LEATHER, 6, 1, 16, 1),
                            new EmeraldForItems(Items.RABBIT_HIDE, 2, 4, 12, 1)
                    },
                    7, new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(Items.BEEF, 1, 10, 12, 1),
                            new ItemsForEmeralds(Items.MUTTON, 1, 7, 12, 1),
                            new ItemsForEmeralds(Items.BREAD, 1, 6, 16, 1)
                    }));
    public static final Int2ObjectMap<VillagerTrades.ItemListing[]> KILTON_TRADES = toIntMap(
            ImmutableMap.of(
                    1, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.DRAGON_EGG, 1, 64, 1, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.PHANTOM_MEMBRANE, 35, 3, Integer.MAX_VALUE, 1, ItemInit.MON.get()) // infinite
                    },
                    2, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.BONE, 64, 1, Integer.MAX_VALUE, 1, ItemInit.MON.get()), // infinite
                            new EmeraldForItems(Items.GUNPOWDER, 64, 1, Integer.MAX_VALUE, 1, ItemInit.MON.get()), // infinite
                            new EmeraldForItems(Items.ROTTEN_FLESH, 64, 1, Integer.MAX_VALUE, 1, ItemInit.MON.get()) // infinite
                    },
                    3, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.STRING, 64, 1, 10, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.SPIDER_EYE, 64, 1, 10, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.FERMENTED_SPIDER_EYE, 30, 2, Integer.MAX_VALUE, 1, ItemInit.MON.get()) // infinite
                    },
                    4, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.SLIME_BALL, 35, 2, 12, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.PRISMARINE_SHARD, 30, 3, Integer.MAX_VALUE, 1, ItemInit.MON.get()) // infinite
                    },
                    5, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.MAGMA_CREAM, 40, 3, 12, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.BLAZE_ROD, 64, 3, 12, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.GHAST_TEAR, 15, 5, Integer.MAX_VALUE, 1, ItemInit.MON.get()) // infinite
                    },
                    6, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.SHULKER_SHELL, 30, 7, 12, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.ENDER_PEARL, 45, 5, 12, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.TOTEM_OF_UNDYING, 1, 20, 3, 1, ItemInit.MON.get())
                    },
                    7, new VillagerTrades.ItemListing[]{
                            new EmeraldForItems(Items.SKELETON_SKULL, 1, 10, 4, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.ZOMBIE_HEAD, 1, 10, 4, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.CREEPER_HEAD, 1, 10, 4, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.PIGLIN_HEAD, 1, 15, 4, 1, ItemInit.MON.get()),
                            new EmeraldForItems(Items.WITHER_SKELETON_SKULL, 1, 15, 4, 1, ItemInit.MON.get())
                    },
                    8, new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(Items.DRAGON_BREATH, 9, 1, 5, 1, ItemInit.MON.get()), // TODO: Monster Extract
                            new ItemsForEmeralds(Items.WOODEN_SHOVEL, 19, 1, 2, 1, ItemInit.MON.get()), // TODO: Wooden Mop
                            new ItemsForEmeralds(ItemInit.BOKOBLIN_HELMET.get(), 30, 1, 2, 1, ItemInit.MON.get()),
                            new ItemsForEmeralds(Items.IRON_AXE, 35, 1, 2, 1, ItemInit.MON.get()) // TODO: Spring-Loaded Hammer
                    },
                    9, new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(ItemInit.MONSTER_HORSE_ARMOR.get(), 50, 1, 2, 1, ItemInit.MON.get())
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
        private Item emerald = Items.EMERALD;

        public EmeraldForItems(ItemLike pItem, int pCost, int pMaxUses, int pVillagerXp, Item emerald) {
            this(pItem, pCost, 1, pMaxUses, pVillagerXp, emerald);
        }

        public EmeraldForItems(ItemLike pItem, int pCost, int pEmeraldCount, int pMaxUses, int pVillagerXp, Item emerald) {
            this(pItem, pCost, pEmeraldCount, pMaxUses, pVillagerXp);
            this.emerald = emerald;
        }

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

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            ItemStack itemstack = new ItemStack(this.item, this.cost);
            return new MerchantOffer(itemstack, new ItemStack(this.emerald, this.emeraldCount), this.maxUses, this.villagerXp, this.priceMultiplier);
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
        private Item emerald = Items.EMERALD;

        public ItemsAndEmeraldsToItems(ItemLike pFromItem, int pFromCount, Item pToItem, int pToCount, int pMaxUses, int pVillagerXp, Item emerald) {
            this(pFromItem, pFromCount, 1, pToItem, pToCount, pMaxUses, pVillagerXp, emerald);
        }

        public ItemsAndEmeraldsToItems(ItemLike pFromItem, int pFromCount, int pEmeraldCost, Item pToItem, int pToCount, int pMaxUses, int pVillagerXp, Item emerald) {
            this(pFromItem, pFromCount, pEmeraldCost, pToItem, pToCount, pMaxUses, pVillagerXp);
            this.emerald = emerald;
        }

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
        @Override
        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            return new MerchantOffer(new ItemStack(this.emerald, this.emeraldCost), new ItemStack(this.fromItem.getItem(), this.fromCount), new ItemStack(this.toItem.getItem(), this.toCount), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class ItemsForEmeralds implements VillagerTrades.ItemListing {
        private final ItemStack itemStack;
        private final int emeraldCost;
        private final int numberOfItems;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;
        private Item emerald = Items.EMERALD;

        public ItemsForEmeralds(Block pBlock, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp, Item emerald) {
            this(new ItemStack(pBlock), pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp, emerald);
        }

        public ItemsForEmeralds(Item pItem, int pEmeraldCost, int pNumberOfItems, int pVillagerXp, Item emerald) {
            this(new ItemStack(pItem), pEmeraldCost, pNumberOfItems, 12, pVillagerXp, emerald);
        }

        public ItemsForEmeralds(Item pItem, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp, Item emerald) {
            this(new ItemStack(pItem), pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp, emerald);
        }

        public ItemsForEmeralds(ItemStack pItemStack, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp, Item emerald) {
            this(pItemStack, pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp, 0.05F, emerald);
        }

        public ItemsForEmeralds(ItemStack pItemStack, int pEmeraldCost, int pNumberOfItems, int pMaxUses, int pVillagerXp, float pPriceMultiplier, Item emerald) {
            this(pItemStack, pEmeraldCost, pNumberOfItems, pMaxUses, pVillagerXp, pPriceMultiplier);
            this.emerald = emerald;
        }

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

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            return new MerchantOffer(new ItemStack(this.emerald, this.emeraldCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.villagerXp, this.priceMultiplier);
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
        private Item emerald = Items.EMERALD;


        public TippedArrowForItemsAndEmeralds(Item pFromItem, int pFromCount, Item pToItem, int pToCount, int pEmeraldCost, int pMaxUses, int pVillagerXp, Item emerald) {
            this(pFromItem, pFromCount, pToItem, pToCount, pEmeraldCost, pMaxUses, pVillagerXp);
            this.emerald = emerald;
        }

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

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            ItemStack itemstack = new ItemStack(this.emerald, this.emeraldCost);
            List<Potion> list = BuiltInRegistries.POTION.stream().filter((potion) -> !potion.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potion)).toList();
            Potion potion = list.get(pRandom.nextInt(list.size()));
            ItemStack itemstack1 = PotionUtils.setPotion(new ItemStack(this.toItem.getItem(), this.toCount), potion);
            return new MerchantOffer(itemstack, new ItemStack(this.fromItem, this.fromCount), itemstack1, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }
}
