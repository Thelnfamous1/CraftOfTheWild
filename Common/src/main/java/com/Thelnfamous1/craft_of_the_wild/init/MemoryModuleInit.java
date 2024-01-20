package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.mojang.serialization.Codec;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;

public class MemoryModuleInit {
    public static final RegistrationProvider<MemoryModuleType<?>> MEMORY_MODULE_TYPES = RegistrationProvider.get(Registries.MEMORY_MODULE_TYPE, Constants.MODID);
    public static final RegistryObject<MemoryModuleType<Boolean>> IS_SLEEPING = MEMORY_MODULE_TYPES.register("is_sleeping",
            () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    public static final RegistryObject<MemoryModuleType<Boolean>> DIG_COOLDOWN = MEMORY_MODULE_TYPES.register("dig_cooldown",
            () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    public static void loadClass() {

    }
}
