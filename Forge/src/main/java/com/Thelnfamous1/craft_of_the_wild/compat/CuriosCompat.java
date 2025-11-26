package com.Thelnfamous1.craft_of_the_wild.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class CuriosCompat {
    public static boolean hasCharmEquipped(LivingEntity entity, Item item, String charmSlotId){
        return CuriosApi.getCuriosInventory(entity)
                .map(ci -> ci.getStacksHandler(charmSlotId))
                .filter(Optional::isPresent)
                .map(sh -> sh.get().getStacks())
                .map(s -> {
                    for(int i = 0; i < s.getSlots(); i++){
                        if(s.getStackInSlot(i).is(item)) return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}
