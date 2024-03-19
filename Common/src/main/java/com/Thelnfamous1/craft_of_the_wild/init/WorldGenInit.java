package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.feature.BurrowPlacement;
import com.Thelnfamous1.craft_of_the_wild.feature.SpawnEntityFeature;
import com.Thelnfamous1.craft_of_the_wild.structure.StoneTalusPiece;
import com.Thelnfamous1.craft_of_the_wild.structure.StoneTalusStructure;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WorldGenInit {

    public static final RegistrationProvider<Feature<?>> FEATURES = RegistrationProvider.get(Registries.FEATURE, Constants.MODID);

    public static final RegistryObject<Feature<SpawnEntityFeature.EntityConfig>> SPAWN_ENTITY = FEATURES.register("spawn_entity", () -> new SpawnEntityFeature(SpawnEntityFeature.EntityConfig.CODEC));

    public static final RegistrationProvider<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = RegistrationProvider.get(Registries.PLACEMENT_MODIFIER_TYPE, Constants.MODID);

    public static final RegistryObject<PlacementModifierType<BurrowPlacement>> BURROW = PLACEMENT_MODIFIER_TYPES.register("burrow", () -> () -> BurrowPlacement.CODEC);

    public static final ResourceKey<PlacedFeature> STONE_TALUS_PF = ResourceKey.create(Registries.PLACED_FEATURE, COTWCommon.getResourceLocation("stone_talus"));
    public static final ResourceKey<ConfiguredFeature<?,?>> STONE_TALUS_CF = ResourceKey.create(Registries.CONFIGURED_FEATURE, COTWCommon.getResourceLocation("stone_talus"));

    // structure types

    public static final RegistrationProvider<StructureType<?>> STRUCTURE_TYPES = RegistrationProvider.get(Registries.STRUCTURE_TYPE, Constants.MODID);

    public static final RegistryObject<StructureType<StoneTalusStructure>> STONE_TALUS_ST = STRUCTURE_TYPES.register("stone_talus", () -> () -> StoneTalusStructure.CODEC);

    // structure
    public static final ResourceKey<Structure> STONE_TALUS_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, COTWCommon.getResourceLocation("stone_talus"));

    // structure set
    public static final ResourceKey<StructureSet> STONE_TALUS_SS = ResourceKey.create(Registries.STRUCTURE_SET, COTWCommon.getResourceLocation("stone_talus"));

    // structure pieces
    public static final RegistrationProvider<StructurePieceType> STRUCTURE_PIECE_TYPES = RegistrationProvider.get(Registries.STRUCTURE_PIECE, Constants.MODID);

    public static final RegistryObject<StructurePieceType> STONE_TALUS_SPT = STRUCTURE_PIECE_TYPES.register("stone_talus", () -> (StructurePieceType.ContextlessType) StoneTalusPiece::new);

    public static void placedFeatures(BootstapContext<PlacedFeature> context) {
        stoneTalus(context, WorldGenInit.STONE_TALUS_PF, WorldGenInit.STONE_TALUS_CF);
    }

    private static void stoneTalus(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> feature, ResourceKey<ConfiguredFeature<?, ?>> configured) {
        context.register(feature, new PlacedFeature(context.lookup(Registries.CONFIGURED_FEATURE).get(configured).get(),
                        List.of(
                                RarityFilter.onAverageOnceEvery(200),
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

    public static void structures(BootstapContext<Structure> context){
        HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
        context.register(STONE_TALUS_STRUCTURE,
                new StoneTalusStructure(
                        new Structure.StructureSettings(
                                biomeLookup.getOrThrow(COTWTags.SPAWNS_STONE_TALUS),
                                Map.of(),
                                GenerationStep.Decoration.SURFACE_STRUCTURES,
                                TerrainAdjustment.NONE)
                        ));
    }

    public static void structureSets(BootstapContext<StructureSet> context){
        HolderGetter<Structure> structureLookup = context.lookup(Registries.STRUCTURE);
        context.register(STONE_TALUS_SS,
                new StructureSet(structureLookup.getOrThrow(STONE_TALUS_STRUCTURE), new RandomSpreadStructurePlacement(20, 10, RandomSpreadType.LINEAR, 14357617)));
    }

    public static void loadClass() {
    }
}