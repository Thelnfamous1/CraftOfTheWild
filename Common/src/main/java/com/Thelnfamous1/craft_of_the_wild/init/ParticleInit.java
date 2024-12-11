package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.mojang.serialization.Codec;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;

import java.util.function.Function;

public class ParticleInit {
    private static final RegistrationProvider<ParticleType<?>> PARTICLE_TYPES = RegistrationProvider.get(Registries.PARTICLE_TYPE, Constants.MODID);

    public static final RegistryObject<ParticleType<BlockParticleOption>> DUST_PILLAR = registerComplexParticleType("dust_pillar", BlockParticleOption.DESERIALIZER, BlockParticleOption::codec);

    private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> registerComplexParticleType(String name, ParticleOptions.Deserializer<T> deserializer, Function<ParticleType<T>, Codec<T>> pCodecFactory) {
        return PARTICLE_TYPES.register(name, () -> new ParticleType<T>(false, deserializer) {
            public Codec<T> codec() {
                return pCodecFactory.apply(this);
            }
        });
    }

    public static void loadClass() {
    }
}
