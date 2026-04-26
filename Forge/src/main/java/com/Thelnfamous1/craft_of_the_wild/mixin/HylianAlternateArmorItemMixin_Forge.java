package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.item.AlternateArmor;
import com.Thelnfamous1.craft_of_the_wild.item.HylianAlternateArmorItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HylianAlternateArmorItem.class)
public abstract class HylianAlternateArmorItemMixin_Forge extends HylianArmorItemMixin_Forge {
    public HylianAlternateArmorItemMixin_Forge(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if(AlternateArmor.usingAlternate(stack)){
            return ((HylianAlternateArmorItem)(Object)this).getArmorTexture(stack, type);
        }
        return super.getArmorTexture(stack, entity, slot, type);
    }
}
