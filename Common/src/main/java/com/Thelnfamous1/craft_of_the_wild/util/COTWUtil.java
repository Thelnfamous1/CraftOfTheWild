package com.Thelnfamous1.craft_of_the_wild.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class COTWUtil {

    public static double getHitboxAdjustedDistance(LivingEntity attacker, LivingEntity target, double distance) {
        return attacker.getBbWidth() * 0.5 + target.getBbWidth() * 0.5 + distance * attacker.getScale();
    }

    public static int secondsToTicks(float seconds){
        return Mth.ceil(seconds * 20);
    }

    public static HolderLookup.Provider createLookup(RegistrySetBuilder builder) {
        RegistryAccess.Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        return builder.build(access);
    }
}
