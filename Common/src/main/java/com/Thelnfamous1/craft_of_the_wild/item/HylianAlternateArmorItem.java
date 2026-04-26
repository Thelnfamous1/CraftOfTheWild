package com.Thelnfamous1.craft_of_the_wild.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HylianAlternateArmorItem extends HylianArmorItem implements AlternateArmor{

    public HylianAlternateArmorItem(ArmorMaterial $$0, Type $$1, Properties $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        super.appendHoverText($$0, $$1, $$2, $$3);
        if(AlternateArmor.usingAlternate($$0)){
            $$2.add(Component.translatable($$0.getDescriptionId() + ".alternate").withStyle(ChatFormatting.GRAY));
        }
    }
}
