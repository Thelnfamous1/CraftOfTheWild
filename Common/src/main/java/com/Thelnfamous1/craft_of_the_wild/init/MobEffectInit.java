package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MobEffectInit {
    public static final RegistrationProvider<MobEffect> MOB_EFFECTS = RegistrationProvider.get(Registries.MOB_EFFECT, Constants.MODID);

    public static final RegistryObject<MobEffect> RADIANT_DISGUISE = MOB_EFFECTS.register("radiant_disguise",
            () -> new COTWMobEffect(MobEffectCategory.BENEFICIAL, 0x58665E));

    public static final RegistryObject<MobEffect> BOKOBLIN_DISGUISE = MOB_EFFECTS.register("bokoblin_disguise",
            () -> new COTWMobEffect(MobEffectCategory.BENEFICIAL, 0x9A2516));


    public static void loadClass() {

    }

    public static class COTWMobEffect extends MobEffect{

        protected COTWMobEffect(MobEffectCategory $$0, int $$1) {
            super($$0, $$1);
        }
    }

}
