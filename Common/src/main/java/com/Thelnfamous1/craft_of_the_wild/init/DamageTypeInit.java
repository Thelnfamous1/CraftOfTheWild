package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DamageTypeInit {

    public static final ResourceKey<DamageType> MOB_AREA_OF_EFFECT_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, COTWCommon.getResourceLocation("mob_area_of_effect_attack"));

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(MOB_AREA_OF_EFFECT_ATTACK, new DamageType("mob", 0.1F));
    }

    public static void loadClass() {
    }

    public static Holder.Reference<DamageType> getHolder(RegistryAccess registryAccess, ResourceKey<DamageType> key) {
        return registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
    }

    public static DamageSource createDamageSource(Entity attacker, ResourceKey<DamageType> key) {
        return new DamageSource(getHolder(attacker.level().registryAccess(), key), attacker);
    }

    public static DamageSource mobAreaOfEffectAttack(LivingEntity attacker) {
        return createDamageSource(attacker, MOB_AREA_OF_EFFECT_ATTACK);
    }
}
