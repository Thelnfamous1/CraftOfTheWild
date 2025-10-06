package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.util.COTWPaintingVariant;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingVariantInit {

    public static final RegistrationProvider<PaintingVariant> PAINTING_VARIANTS = RegistrationProvider.get(Registries.PAINTING_VARIANT, Constants.MODID);

    public static final RegistryObject<PaintingVariant> COOKIE_RECIPE = PAINTING_VARIANTS.register("cookie_recipe", () -> new COTWPaintingVariant(32, 48, "backupcup"));
    public static final RegistryObject<PaintingVariant> EGG_PUDDING_RECIPE = PAINTING_VARIANTS.register("egg_pudding_recipe", () -> new COTWPaintingVariant(32, 48, "backupcup"));
    public static final RegistryObject<PaintingVariant> PUMPKIN_PIE_RECIPE = PAINTING_VARIANTS.register("pumpkin_pie_recipe", () -> new COTWPaintingVariant(32, 48, "backupcup"));
    public static final RegistryObject<PaintingVariant> STABLES_SIGN = PAINTING_VARIANTS.register("stables_sign", () -> new COTWPaintingVariant(48, 32, "backupcup"));

    public static void loadClass() {
    }
}
