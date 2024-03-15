package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.feature.BurrowPlacement;
import com.Thelnfamous1.craft_of_the_wild.feature.SpawnEntityFeature;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;
import java.util.Optional;

public class WorldGenInit {

    public static final RegistrationProvider<Feature<?>> FEATURES = RegistrationProvider.get(Registries.FEATURE, Constants.MODID);

    public static final RegistryObject<Feature<SpawnEntityFeature.EntityConfig>> SPAWN_ENTITY = FEATURES.register("spawn_entity", () -> new SpawnEntityFeature(SpawnEntityFeature.EntityConfig.CODEC));

    public static final RegistrationProvider<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = RegistrationProvider.get(Registries.PLACEMENT_MODIFIER_TYPE, Constants.MODID);

    public static final RegistryObject<PlacementModifierType<BurrowPlacement>> BURROW = PLACEMENT_MODIFIER_TYPES.register("burrow", () -> () -> BurrowPlacement.CODEC);

    public static final ResourceKey<PlacedFeature> STONE_TALUS_PF = ResourceKey.create(Registries.PLACED_FEATURE, COTWCommon.getResourceLocation("stone_talus"));
    public static final ResourceKey<ConfiguredFeature<?,?>> STONE_TALUS_CF = ResourceKey.create(Registries.CONFIGURED_FEATURE, COTWCommon.getResourceLocation("stone_talus"));

    public static void placedFeatures(BootstapContext<PlacedFeature> context) {
        stoneTalus(context, WorldGenInit.STONE_TALUS_PF, WorldGenInit.STONE_TALUS_CF);
    }

    private static void stoneTalus(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> feature, ResourceKey<ConfiguredFeature<?, ?>> configured) {
        context.register(feature, new PlacedFeature(context.lookup(Registries.CONFIGURED_FEATURE).get(configured).get(),
                        List.of(
                                RarityFilter.onAverageOnceEvery(45),
                                InSquarePlacement.spread(),
                                PlacementUtils.HEIGHTMAP,
                                new BurrowPlacement(EntityInit.STONE_TALUS.get()),
                                BiomeFilter.biome()
                        )
                )
        );
    }

    public static void configuredFeature(BootstapContext<ConfiguredFeature<?, ?>> context) {
        stoneTalus(context, WorldGenInit.STONE_TALUS_CF);
    }

    private static void stoneTalus(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key) {
        context.register(key, new ConfiguredFeature<>(
                SPAWN_ENTITY.get(),
                new SpawnEntityFeature.EntityConfig(EntityInit.STONE_TALUS.get(), Optional.empty())
                )
        );
    }

    public static void loadClass() {
    }
}