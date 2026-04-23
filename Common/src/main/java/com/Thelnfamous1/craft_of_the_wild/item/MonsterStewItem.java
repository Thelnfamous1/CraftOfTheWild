package com.Thelnfamous1.craft_of_the_wild.item;

import com.Thelnfamous1.craft_of_the_wild.recipe.PotionInfusionRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MonsterStewItem extends BowlFoodItem {

    public MonsterStewItem(Properties $$0) {
        super($$0);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack usedItemStack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(usedItemStack, level, entity);
        PotionInfusionRecipe.applyPotionEffects(level, entity, PotionInfusionRecipe.getInfusedPotion(usedItemStack));
        return result;
    }

    @Override
    public Component getName(ItemStack $$0) {
        if(PotionInfusionRecipe.hasInfusedPotion($$0)){
            Potion infusedPotion = PotionInfusionRecipe.getInfusedPotion($$0);
            return Component.translatable(PotionInfusionRecipe.extendDescriptionIdWithInfused($$0), Component.translatable(PotionInfusionRecipe.getPotionDescriptionId(infusedPotion)));
        }
        return super.getName($$0);
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        PotionInfusionRecipe.addInfusedPotionTooltip($$0, $$2, 1.0F);
    }
}
