package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.recipe.AlternateArmorRecipe;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class RecipeSerializerInit {
    public static final RegistrationProvider<RecipeSerializer<?>> RECIPE_SERIALIZERS = RegistrationProvider.get(Registries.RECIPE_SERIALIZER, Constants.MODID);

    public static final RegistryObject<RecipeSerializer<AlternateArmorRecipe>> ALTERNATE_ARMOR = RECIPE_SERIALIZERS.register("alternate_armor", () -> new SimpleCraftingRecipeSerializer<>(AlternateArmorRecipe::new));

    public static void loadClass() {

    }
}
