package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.item.HylianAlternateArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HylianAlternateArmorItem.class)
public abstract class HylianAlternateArmorItemMixin_Fabric extends HylianArmorItemMixin_Fabric {
    public HylianAlternateArmorItemMixin_Fabric(ArmorMaterial pMaterial, ArmorItem.Type pType, Item.Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }
}
