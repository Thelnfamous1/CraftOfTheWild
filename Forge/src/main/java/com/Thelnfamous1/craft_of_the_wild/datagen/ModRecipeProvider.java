package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.init.RecipeSerializerInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
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
        SpecialRecipeBuilder.special(RecipeSerializerInit.POTION_INFUSION.get()).save(recipeSaver, String.valueOf(RecipeSerializerInit.POTION_INFUSION.getId()));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PURPLE_DYE, 3).requires(ItemInit.MONSTER_EXTRACT.get())
                .unlockedBy("has_monster_extract", has(ItemInit.MONSTER_EXTRACT.get())).save(recipeSaver);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemInit.MONSTER_STEW.get())
                .requires(ItemInit.MONSTER_EXTRACT.get())
                .requires(COTWTags.RAW_MEAT)
                .requires(COTWTags.RAW_FISH)
                .requires(COTWTags.MUSHROOMS)
                .requires(Items.BOWL)
                .unlockedBy("has_monster_extract", has(ItemInit.MONSTER_EXTRACT.get())).save(recipeSaver);


        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemInit.MONSTER_CAKE.get())
                .pattern("XMX")
                .pattern("SES")
                .pattern("WWW")
                .define('X', ItemInit.MONSTER_EXTRACT.get())
                .define('M', Items.MILK_BUCKET)
                .define('S', Items.SUGAR)
                .define('E', Items.EGG)
                .define('W', Items.WHEAT)
                .unlockedBy("has_monster_extract", has(ItemInit.MONSTER_EXTRACT.get()))
                .save(recipeSaver);

    }
}
