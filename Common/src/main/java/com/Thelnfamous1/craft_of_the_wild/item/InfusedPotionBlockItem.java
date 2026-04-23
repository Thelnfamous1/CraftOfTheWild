package com.Thelnfamous1.craft_of_the_wild.item;

import com.Thelnfamous1.craft_of_the_wild.recipe.PotionInfusionRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;

public class InfusedPotionBlockItem extends BlockItem {
    public InfusedPotionBlockItem(Block $$0, Properties $$1) {
        super($$0, $$1);
    }

    @Override
    public Component getName(ItemStack $$0) {
        if(PotionInfusionRecipe.hasInfusedPotion($$0)){
            Potion infusedPotion = PotionInfusionRecipe.getInfusedPotion($$0);
            return Component.translatable(PotionInfusionRecipe.extendDescriptionIdWithInfused($$0), Component.translatable(PotionInfusionRecipe.getPotionDescriptionId(infusedPotion)));
        }
        return super.getName($$0);
    }

}
