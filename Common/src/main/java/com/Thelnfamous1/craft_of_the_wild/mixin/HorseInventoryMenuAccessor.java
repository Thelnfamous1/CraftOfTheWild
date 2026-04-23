package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.inventory.HorseInventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseInventoryMenu.class)
public interface HorseInventoryMenuAccessor {

    @Accessor("horse")
    AbstractHorse craft_of_the_wild$getHorse();
}
