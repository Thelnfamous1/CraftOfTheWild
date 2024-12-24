package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput generator) {
        super(generator);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeSaver) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemInit.EGG_PUDDING.get())
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .requires(Items.SUGAR)
                .requires(Items.BOWL)
                .unlockedBy("has_egg", has(Items.EGG))
                .save(recipeSaver);
    }
}
