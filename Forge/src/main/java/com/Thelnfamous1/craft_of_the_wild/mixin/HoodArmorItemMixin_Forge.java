package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.item.AlternateArmorItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AlternateArmorItem.class)
public abstract class HoodArmorItemMixin_Forge extends ArmorItem {
    public HoodArmorItemMixin_Forge(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if(AlternateArmorItem.usingAlternate(stack)){
            return ((AlternateArmorItem)(Object)this).getArmorTexture(stack, type);
        }
        return super.getArmorTexture(stack, entity, slot, type);
    }
}
