package com.Thelnfamous1.craft_of_the_wild.compat.customvillagertrades;

import com.Thelnfamous1.craft_of_the_wild.entity.COTWVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.apache.commons.lang3.ArrayUtils;
import uk.co.dotcode.customvillagertrades.ConfigHandler;
import uk.co.dotcode.customvillagertrades.TradeUtil;

import java.util.HashMap;
import java.util.OptionalInt;

public class CustomVillagerTradesCompat {

    public static VillagerTrades.ItemListing[] replaceItemListings(COTWVillager villager, VillagerTrades.ItemListing[] originalEntries, int tradeSlot) {
        VillagerProfession profession = villager.getProfession();
        if (ConfigHandler.registeredCustomTrades.containsKey(TradeUtil.getKeyFromProfession(profession))) {
            boolean shouldRemoveAll = ConfigHandler.customTrades.get(TradeUtil.getKeyFromProfession(profession)).removeOtherTrades;
            VillagerTrades.ItemListing[] customTrades = getApplicableTrades(profession, tradeSlot);
            if (shouldRemoveAll) {
                return customTrades;
            } else {
                return ArrayUtils.addAll(originalEntries, customTrades);
            }
        } else {
            return originalEntries;
        }
    }

    private static VillagerTrades.ItemListing[] getApplicableTrades(VillagerProfession profession, int tradeSlot) {
        VillagerTrades.ItemListing[] addedEntries = (VillagerTrades.ItemListing[])((HashMap)ConfigHandler.registeredCustomTrades.get(TradeUtil.getKeyFromProfession(profession))).get(tradeSlot);
        VillagerTrades.ItemListing[] allCustomEntries = addedEntries;
        if (ConfigHandler.registeredAllCategoryTrades != null && ConfigHandler.registeredAllCategoryTrades.containsKey(tradeSlot)) {
            VillagerTrades.ItemListing[] addedAllCategoryEntries = ConfigHandler.registeredAllCategoryTrades.get(tradeSlot);
            allCustomEntries = ArrayUtils.addAll(addedEntries, addedAllCategoryEntries);
        }

        return allCustomEntries;
    }

    public static OptionalInt getMaxOffers(COTWVillager villager) {
        VillagerProfession profession = villager.getProfession();
        if (ConfigHandler.customTrades.get(TradeUtil.getKeyFromProfession(profession)) != null) {
            return OptionalInt.of(ConfigHandler.customTrades.get(TradeUtil.getKeyFromProfession(profession)).maxTrades);
        }
        return OptionalInt.empty();
    }
}
