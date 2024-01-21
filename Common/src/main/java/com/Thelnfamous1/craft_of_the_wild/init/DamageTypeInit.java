package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusArm;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class DamageTypeInit {

    public static final ResourceKey<DamageType> MOB_AREA_OF_EFFECT_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, COTWCommon.getResourceLocation("mob_area_of_effect_attack"));

    public static final ResourceKey<DamageType> STONE_TALUS_ARM = ResourceKey.create(Registries.DAMAGE_TYPE, COTWCommon.getResourceLocation("stone_talus_arm"));

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(MOB_AREA_OF_EFFECT_ATTACK, new DamageType("mob", 0.1F));
        context.register(STONE_TALUS_ARM, new DamageType(STONE_TALUS_ARM.location().toLanguageKey(), 0.1F));
    }

    public static void loadClass() {
    }

    public static Holder.Reference<DamageType> getHolder(RegistryAccess registryAccess, ResourceKey<DamageType> key) {
        return registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
    }

    public static DamageSource createDamageSource(ResourceKey<DamageType> key, Entity attacker) {
        return new DamageSource(getHolder(attacker.level().registryAccess(), key), attacker);
    }

    public static DamageSource createDamageSource(ResourceKey<DamageType> key, Entity projectile, Entity attacker) {
        return new DamageSource(getHolder(projectile.level().registryAccess(), key), projectile, attacker);
    }

    public static DamageSource mobAreaOfEffectAttack(LivingEntity attacker) {
        return createDamageSource(MOB_AREA_OF_EFFECT_ATTACK, attacker);
    }

    public static DamageSource stoneTalusArm(StoneTalusArm arm, @Nullable Entity shooter) {
        return createDamageSource(STONE_TALUS_ARM, arm, shooter);
    }
}
