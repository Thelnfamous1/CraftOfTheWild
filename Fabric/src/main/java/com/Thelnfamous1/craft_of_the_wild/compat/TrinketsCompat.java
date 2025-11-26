package com.Thelnfamous1.craft_of_the_wild.compat;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class TrinketsCompat {
    public static boolean hasCharmEquipped(LivingEntity entity, Item item, String charmSlotId){
        return TrinketsApi.getTrinketComponent(entity).map(tc -> tc.isEquipped(item)).orElse(false);
    }
}
