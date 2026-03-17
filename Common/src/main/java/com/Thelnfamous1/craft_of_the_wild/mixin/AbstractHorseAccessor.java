package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractHorse.class)
public interface AbstractHorseAccessor {

    @Accessor("inventory")
    SimpleContainer craft_of_the_wild$getInventory();
}
