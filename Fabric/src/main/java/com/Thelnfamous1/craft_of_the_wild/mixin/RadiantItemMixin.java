package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.item.RadiantItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RadiantItem.class)
public abstract class RadiantItemMixin extends Item {
    public RadiantItemMixin(Properties properties) {
        super(properties);
    }
}
