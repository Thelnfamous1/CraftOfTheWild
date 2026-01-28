package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.criterion.COTWTradeTrigger;
import com.Thelnfamous1.craft_of_the_wild.criterion.KilledCountTrigger;
import com.Thelnfamous1.craft_of_the_wild.mixin.CriteriaTriggersAccessor;
import com.google.common.collect.Maps;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class CriterionInit {

    private static final Map<ResourceLocation, CriterionTrigger<?>> CRITERIA = Maps.newHashMap();
    public static final KilledCountTrigger PLAYER_KILLED_ENTITY_COUNT = register(new KilledCountTrigger(COTWCommon.getResourceLocation("player_killed_entity_count")));
    public static final COTWTradeTrigger TRADE = register(new COTWTradeTrigger(COTWCommon.getResourceLocation("villager_trade")));


    private static <T extends CriterionTrigger<?>> T register(T $$0) {
        if (CRITERIA.containsKey($$0.getId())) {
            throw new IllegalArgumentException("Duplicate criterion id " + $$0.getId());
        } else {
            CRITERIA.put($$0.getId(), $$0);
            return $$0;
        }
    }

    public static void registerAll() {
        CRITERIA.forEach((location, criterionTrigger) -> CriteriaTriggersAccessor.craft_of_the_wild$callRegister(criterionTrigger));
    }
}
