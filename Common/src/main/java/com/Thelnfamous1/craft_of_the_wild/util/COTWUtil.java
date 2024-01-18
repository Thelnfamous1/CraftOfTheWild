package com.Thelnfamous1.craft_of_the_wild.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class COTWUtil {

    public static double getHitboxAdjustedDistance(LivingEntity attacker, LivingEntity target, double distance) {
        return attacker.getBbWidth() * 0.5 + target.getBbWidth() * 0.5 + distance;
    }

    public static int secondsToTicks(float seconds){
        return Mth.ceil(seconds * 20);
    }

    public static HolderLookup.Provider createLookup(RegistrySetBuilder builder) {
        RegistryAccess.Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        return builder.build(access);
    }

    public static void sendHitboxParticles(AABB attackBox, Level level) {
        if(level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, attackBox.minX, attackBox.minY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(ParticleTypes.FLAME, attackBox.maxX, attackBox.minY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(ParticleTypes.FLAME, attackBox.minX, attackBox.minY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(ParticleTypes.FLAME, attackBox.maxX, attackBox.minY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(ParticleTypes.FLAME, attackBox.minX, attackBox.maxY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(ParticleTypes.FLAME, attackBox.maxX, attackBox.maxY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(ParticleTypes.FLAME, attackBox.minX, attackBox.maxY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(ParticleTypes.FLAME, attackBox.maxX, attackBox.maxY, attackBox.minZ, 0, 0, 0, 0, 1);
        }
    }
}
