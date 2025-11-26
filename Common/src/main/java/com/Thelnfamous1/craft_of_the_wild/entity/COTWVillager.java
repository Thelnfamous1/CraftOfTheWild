package com.Thelnfamous1.craft_of_the_wild.entity;

import com.google.common.collect.Sets;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.Set;

public interface COTWVillager extends Merchant {

    static <T extends LivingEntity> void addOffersFromitemListings(T villager, MerchantOffers merchantOffers, VillagerTrades.ItemListing[] potentialTrades, int maxOffers) {
        Set<Integer> indices = Sets.newHashSet();
        if (potentialTrades.length > maxOffers) {
            while(indices.size() < maxOffers) {
                indices.add(villager.getRandom().nextInt(potentialTrades.length));
            }
        } else {
            for(int i = 0; i < potentialTrades.length; ++i) {
                indices.add(i);
            }
        }

        for(Integer index : indices) {
            VillagerTrades.ItemListing trade = potentialTrades[index];
            MerchantOffer offer = trade.getOffer(villager, villager.getRandom());
            if (offer != null) {
                merchantOffers.add(offer);
            }
        }
    }

    void addOffersFromItemListings(MerchantOffers merchantOffers, VillagerTrades.ItemListing[] potentialTrades, int maxOffers);

    VillagerProfession getProfession();

    int getTradeSlots();

    int getMaxOffersPerSlot();
}
