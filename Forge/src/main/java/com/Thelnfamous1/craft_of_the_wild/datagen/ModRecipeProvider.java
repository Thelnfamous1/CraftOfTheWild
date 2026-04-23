package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.init.RecipeSerializerInit;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
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

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemInit.RADIANT_HELMET.get())
                .pattern("SSS")
                .pattern("L L")
                .define('S', ItemInit.LUMINOUS_STONE.get())
                .define('L', Items.LEATHER)
                .unlockedBy("has_luminous_stone", has(ItemInit.LUMINOUS_STONE.get()))
                .save(recipeSaver);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemInit.RADIANT_CHESTPLATE.get())
                .pattern("S S")
                .pattern("LSL")
                .pattern("LSL")
                .define('S', ItemInit.LUMINOUS_STONE.get())
                .define('L', Items.LEATHER)
                .unlockedBy("has_luminous_stone", has(ItemInit.LUMINOUS_STONE.get()))
                .save(recipeSaver);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemInit.RADIANT_LEGGINGS.get())
                .pattern("SLS")
                .pattern("L L")
                .pattern("L L")
                .define('S', ItemInit.LUMINOUS_STONE.get())
                .define('L', Items.LEATHER)
                .unlockedBy("has_luminous_stone", has(ItemInit.LUMINOUS_STONE.get()))
                .save(recipeSaver);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemInit.RADIANT_BOOTS.get())
                .pattern("S S")
                .pattern("L L")
                .define('S', ItemInit.LUMINOUS_STONE.get())
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(ItemInit.LUMINOUS_STONE.get()))
                .save(recipeSaver);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemInit.HYLIAN_HELMET.get())
                .pattern("LHL")
                .pattern("L L")
                .define('H', Items.RABBIT_HIDE)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(recipeSaver);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemInit.HYLIAN_CHESTPLATE.get())
                .pattern("I I")
                .pattern("HIH")
                .pattern("LLL")
                .define('H', Items.RABBIT_HIDE)
                .define('L', Items.LEATHER)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(recipeSaver);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemInit.HYLIAN_LEGGINGS.get())
                .pattern("ILI")
                .pattern("L L")
                .pattern("L L")
                .define('L', Items.LEATHER)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(recipeSaver);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemInit.HYLIAN_BOOTS.get())
                .pattern("H H")
                .pattern("L L")
                .define('H', Items.RABBIT_HIDE)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(recipeSaver);


        SpecialRecipeBuilder.special(RecipeSerializerInit.ALTERNATE_ARMOR.get()).save(recipeSaver, String.valueOf(RecipeSerializerInit.ALTERNATE_ARMOR.getId()));
    }
}
