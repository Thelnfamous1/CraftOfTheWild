package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.feature.BurrowPlacement;
import com.Thelnfamous1.craft_of_the_wild.feature.SpawnEntityFeature;
import com.Thelnfamous1.craft_of_the_wild.structure.StoneTalusPiece;
import com.Thelnfamous1.craft_of_the_wild.structure.StoneTalusStructure;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

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
    public static final ResourceKey<Structure> STABLES_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, COTWCommon.getResourceLocation("stables"));
    public static final ResourceKey<Structure> STABLES_SNOWY_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, COTWCommon.getResourceLocation("stables_snowy"));

    // structure set
    public static final ResourceKey<StructureSet> STONE_TALUS_SS = ResourceKey.create(Registries.STRUCTURE_SET, COTWCommon.getResourceLocation("stone_talus"));
    public static final ResourceKey<StructureSet> STABLES_SS = ResourceKey.create(Registries.STRUCTURE_SET, COTWCommon.getResourceLocation("stables"));

    // structure pieces
    public static final RegistrationProvider<StructurePieceType> STRUCTURE_PIECE_TYPES = RegistrationProvider.get(Registries.STRUCTURE_PIECE, Constants.MODID);

    public static final RegistryObject<StructurePieceType> STONE_TALUS_SPT = STRUCTURE_PIECE_TYPES.register("stone_talus", () -> (StructurePieceType.ContextlessType) StoneTalusPiece::new);

    // structure template pools
    public static final ResourceKey<StructureTemplatePool> STABLES_STP = ResourceKey.create(Registries.TEMPLATE_POOL, COTWCommon.getResourceLocation("stables"));
    public static final ResourceKey<StructureTemplatePool> STABLES_SNOWY_STP = ResourceKey.create(Registries.TEMPLATE_POOL, COTWCommon.getResourceLocation("stables_snowy"));


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
        HolderGetter<StructureTemplatePool> templatePoolLookup = context.lookup(Registries.TEMPLATE_POOL);
        /*
        context.register(STONE_TALUS_STRUCTURE,
                new StoneTalusStructure(
                        new Structure.StructureSettings(
                                biomeLookup.getOrThrow(COTWTags.SPAWNS_STONE_TALUS),
                                Map.of(),
                                GenerationStep.Decoration.SURFACE_STRUCTURES,
                                TerrainAdjustment.NONE)
                        ));
         */
        context.register(STABLES_STRUCTURE, new JigsawStructure(
                structure(biomeLookup.getOrThrow(COTWTags.HAS_STABLES),
                        TerrainAdjustment.BEARD_THIN),
                templatePoolLookup.getOrThrow(STABLES_STP),
                1,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                true,
                Heightmap.Types.WORLD_SURFACE_WG));
        context.register(STABLES_SNOWY_STRUCTURE, new JigsawStructure(
                structure(biomeLookup.getOrThrow(COTWTags.HAS_STABLES_SNOWY),
                        TerrainAdjustment.BEARD_THIN),
                templatePoolLookup.getOrThrow(STABLES_SNOWY_STP),
                1,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                true,
                Heightmap.Types.WORLD_SURFACE_WG));

    }
    private static Structure.StructureSettings structure(HolderSet<Biome> pBiomes, Map<MobCategory, StructureSpawnOverride> pSpawnOverrides, GenerationStep.Decoration pStep, TerrainAdjustment pTerrainAdaptation) {
        return new Structure.StructureSettings(pBiomes, pSpawnOverrides, pStep, pTerrainAdaptation);
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> pBiomes, GenerationStep.Decoration pStep, TerrainAdjustment pTerrainAdaptation) {
        return structure(pBiomes, Map.of(), pStep, pTerrainAdaptation);
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> pBiomes, TerrainAdjustment pTerrainAdaptation) {
        return structure(pBiomes, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, pTerrainAdaptation);
    }

    public static void structureSets(BootstapContext<StructureSet> context){
        HolderGetter<Structure> structureLookup = context.lookup(Registries.STRUCTURE);
        /*
        context.register(STONE_TALUS_SS,
                new StructureSet(structureLookup.getOrThrow(STONE_TALUS_STRUCTURE), new RandomSpreadStructurePlacement(20, 10, RandomSpreadType.LINEAR, 14357617)));
         */
        context.register(
                STABLES_SS,
                new StructureSet(List.of(
                        StructureSet.entry(structureLookup.getOrThrow(STABLES_STRUCTURE)),
                        StructureSet.entry(structureLookup.getOrThrow(STABLES_SNOWY_STRUCTURE))),
                        new RandomSpreadStructurePlacement(
                                34, 8,
                                RandomSpreadType.LINEAR, 10387312)));
    }

    public static void templatePools(BootstapContext<StructureTemplatePool> context){
        HolderGetter<StructureTemplatePool> templatePoolLookup = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> emptyPool = templatePoolLookup.getOrThrow(Pools.EMPTY);
        context.register(STABLES_STP,
                new StructureTemplatePool(
                        emptyPool,
                        ImmutableList.of(
                                Pair.of(StructurePoolElement.legacy(COTWCommon.getResourceLocation("stables/v1").toString()), 1),
                                Pair.of(StructurePoolElement.legacy(COTWCommon.getResourceLocation("stables/v2").toString()), 1),
                                Pair.of(StructurePoolElement.legacy(COTWCommon.getResourceLocation("stables/v3").toString()), 1),
                                Pair.of(StructurePoolElement.legacy(COTWCommon.getResourceLocation("stables/v4").toString()), 1)),
                        StructureTemplatePool.Projection.RIGID));
        context.register(STABLES_SNOWY_STP,
                new StructureTemplatePool(
                        emptyPool,
                        ImmutableList.of(
                                Pair.of(StructurePoolElement.legacy(COTWCommon.getResourceLocation("stables/v1_snowy").toString()), 1),
                                Pair.of(StructurePoolElement.legacy(COTWCommon.getResourceLocation("stables/v2_snowy").toString()), 1),
                                Pair.of(StructurePoolElement.legacy(COTWCommon.getResourceLocation("stables/v3_snowy").toString()), 1),
                                Pair.of(StructurePoolElement.legacy(COTWCommon.getResourceLocation("stables/v4_snowy").toString()), 1)),
                        StructureTemplatePool.Projection.RIGID));

    }

    public static void loadClass() {
    }
}