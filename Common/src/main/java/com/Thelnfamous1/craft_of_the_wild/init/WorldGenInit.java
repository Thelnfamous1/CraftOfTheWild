package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.feature.BurrowPlacement;
import com.Thelnfamous1.craft_of_the_wild.feature.CustomFeaturePoolElement;
import com.Thelnfamous1.craft_of_the_wild.feature.SpawnEntityFeature;
import com.Thelnfamous1.craft_of_the_wild.structure.StoneTalusPiece;
import com.Thelnfamous1.craft_of_the_wild.structure.StoneTalusStructure;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class WorldGenInit {

    public static final RegistrationProvider<Feature<?>> FEATURES = RegistrationProvider.get(Registries.FEATURE, Constants.MODID);

    public static final RegistryObject<Feature<SpawnEntityFeature.EntityConfig>> SPAWN_ENTITY = FEATURES.register("spawn_entity", () -> new SpawnEntityFeature(SpawnEntityFeature.EntityConfig.CODEC));

    public static final RegistrationProvider<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = RegistrationProvider.get(Registries.PLACEMENT_MODIFIER_TYPE, Constants.MODID);

    public static final RegistryObject<PlacementModifierType<BurrowPlacement>> BURROW = PLACEMENT_MODIFIER_TYPES.register("burrow", () -> () -> BurrowPlacement.CODEC);

    public static final ResourceKey<PlacedFeature> STONE_TALUS_PF = ResourceKey.create(Registries.PLACED_FEATURE, COTWCommon.getResourceLocation("stone_talus"));
    public static final ResourceKey<ConfiguredFeature<?,?>> STONE_TALUS_CF = ResourceKey.create(Registries.CONFIGURED_FEATURE, COTWCommon.getResourceLocation("stone_talus"));

    public static final ResourceKey<PlacedFeature> VILLAGER_PF = ResourceKey.create(Registries.PLACED_FEATURE, COTWCommon.getResourceLocation("villager"));
    public static final ResourceKey<ConfiguredFeature<?,?>> VILLAGER_CF = ResourceKey.create(Registries.CONFIGURED_FEATURE, COTWCommon.getResourceLocation("villager"));
    public static final ResourceKey<PlacedFeature> BEEDLE_PF = ResourceKey.create(Registries.PLACED_FEATURE, COTWCommon.getResourceLocation("beedle"));
    public static final ResourceKey<ConfiguredFeature<?,?>> BEEDLE_CF = ResourceKey.create(Registries.CONFIGURED_FEATURE, COTWCommon.getResourceLocation("beedle"));
    public static final ResourceKey<PlacedFeature> KASS_PF = ResourceKey.create(Registries.PLACED_FEATURE, COTWCommon.getResourceLocation("kass"));
    public static final ResourceKey<ConfiguredFeature<?,?>> KASS_CF = ResourceKey.create(Registries.CONFIGURED_FEATURE, COTWCommon.getResourceLocation("kass"));
    public static final ResourceKey<PlacedFeature> HORSE_PF = ResourceKey.create(Registries.PLACED_FEATURE, COTWCommon.getResourceLocation("horse"));
    public static final ResourceKey<ConfiguredFeature<?,?>> HORSE_CF = ResourceKey.create(Registries.CONFIGURED_FEATURE, COTWCommon.getResourceLocation("horse"));
    public static final ResourceKey<PlacedFeature> DONKEY_PF = ResourceKey.create(Registries.PLACED_FEATURE, COTWCommon.getResourceLocation("donkey"));
    public static final ResourceKey<ConfiguredFeature<?,?>> DONKEY_CF = ResourceKey.create(Registries.CONFIGURED_FEATURE, COTWCommon.getResourceLocation("donkey"));

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
    public static final ResourceKey<StructureTemplatePool> VILLAGER_STP = ResourceKey.create(Registries.TEMPLATE_POOL, COTWCommon.getResourceLocation("villager"));
    public static final ResourceKey<StructureTemplatePool> BEEDLE_STP = ResourceKey.create(Registries.TEMPLATE_POOL, COTWCommon.getResourceLocation("beedle"));
    public static final ResourceKey<StructureTemplatePool> KASS_RARE_STP = ResourceKey.create(Registries.TEMPLATE_POOL, COTWCommon.getResourceLocation("kass_rare"));
    public static final ResourceKey<StructureTemplatePool> KASS_COMMON_STP = ResourceKey.create(Registries.TEMPLATE_POOL, COTWCommon.getResourceLocation("kass_common"));
    public static final ResourceKey<StructureTemplatePool> HORSE_STP = ResourceKey.create(Registries.TEMPLATE_POOL, COTWCommon.getResourceLocation("horse"));
    public static final ResourceKey<StructureTemplatePool> DONKEY_STP = ResourceKey.create(Registries.TEMPLATE_POOL, COTWCommon.getResourceLocation("donkey"));


    public static void placedFeatures(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> cfLookup = context.lookup(Registries.CONFIGURED_FEATURE);
        stoneTalus(context, cfLookup.get(WorldGenInit.STONE_TALUS_CF).get());
        context.register(VILLAGER_PF, new PlacedFeature(cfLookup.get(VILLAGER_CF).get(), List.of()));
        context.register(BEEDLE_PF, new PlacedFeature(cfLookup.get(BEEDLE_CF).get(), List.of()));
        context.register(KASS_PF, new PlacedFeature(cfLookup.get(KASS_CF).get(), List.of()));
        context.register(HORSE_PF, new PlacedFeature(cfLookup.get(HORSE_CF).get(), List.of()));
        context.register(DONKEY_PF, new PlacedFeature(cfLookup.get(DONKEY_CF).get(), List.of()));
    }

    private static void stoneTalus(BootstapContext<PlacedFeature> context, Holder.Reference<ConfiguredFeature<?, ?>> holder) {
        context.register(WorldGenInit.STONE_TALUS_PF, new PlacedFeature(holder,
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
        spawnEntity(context, WorldGenInit.STONE_TALUS_CF, EntityInit.STONE_TALUS.get());
        spawnEntity(context, WorldGenInit.VILLAGER_CF, EntityType.VILLAGER);
        spawnEntity(context, WorldGenInit.BEEDLE_CF, EntityInit.BEEDLE.get());
        spawnEntity(context, WorldGenInit.KASS_CF, EntityInit.KASS.get());
        spawnEntity(context, WorldGenInit.HORSE_CF, EntityType.HORSE);
        spawnEntity(context, WorldGenInit.DONKEY_CF, EntityType.DONKEY);
    }

    private static void spawnEntity(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, EntityType<?> entityType) {
        context.register(key, new ConfiguredFeature<>(
                SPAWN_ENTITY.get(),
                new SpawnEntityFeature.EntityConfig(entityType, Optional.empty())
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
        /* // TODO: These are no longer datagenned as the mod uses an externally created datapack
        context.register(STABLES_STRUCTURE, new JigsawStructure(
                structure(biomeLookup.getOrThrow(COTWTags.HAS_STABLES),
                        TerrainAdjustment.BEARD_THIN),
                templatePoolLookup.getOrThrow(STABLES_STP),
                3,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                false,
                Heightmap.Types.WORLD_SURFACE_WG));
        context.register(STABLES_SNOWY_STRUCTURE, new JigsawStructure(
                structure(biomeLookup.getOrThrow(COTWTags.HAS_STABLES_SNOWY),
                        TerrainAdjustment.BEARD_THIN),
                templatePoolLookup.getOrThrow(STABLES_SNOWY_STP),
                1,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                false,
                Heightmap.Types.WORLD_SURFACE_WG));
         */

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
                                RandomSpreadType.LINEAR, 49016397)));
    }

    public static void templatePools(BootstapContext<StructureTemplatePool> context){
        HolderGetter<StructureTemplatePool> templatePoolLookup = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<PlacedFeature> pfLookup = context.lookup(Registries.PLACED_FEATURE);
        Holder<StructureTemplatePool> emptyPool = templatePoolLookup.getOrThrow(Pools.EMPTY);
        context.register(STABLES_STP,
                new StructureTemplatePool(
                        emptyPool,
                        ImmutableList.of(
                                Pair.of(StructurePoolElement.single(COTWCommon.getResourceLocation("stables/v1").toString()), 1),
                                Pair.of(StructurePoolElement.single(COTWCommon.getResourceLocation("stables/v2").toString()), 1),
                                Pair.of(StructurePoolElement.single(COTWCommon.getResourceLocation("stables/v3").toString()), 1),
                                Pair.of(StructurePoolElement.single(COTWCommon.getResourceLocation("stables/v4").toString()), 1)),
                        StructureTemplatePool.Projection.RIGID));
        context.register(STABLES_SNOWY_STP,
                new StructureTemplatePool(
                        emptyPool,
                        ImmutableList.of(
                                Pair.of(StructurePoolElement.single(COTWCommon.getResourceLocation("stables/v1_snowy").toString()), 1),
                                Pair.of(StructurePoolElement.single(COTWCommon.getResourceLocation("stables/v2_snowy").toString()), 1),
                                Pair.of(StructurePoolElement.single(COTWCommon.getResourceLocation("stables/v3_snowy").toString()), 1),
                                Pair.of(StructurePoolElement.single(COTWCommon.getResourceLocation("stables/v4_snowy").toString()), 1)),
                        StructureTemplatePool.Projection.RIGID));
        context.register(VILLAGER_STP, singleFeaturePool(emptyPool, pfLookup.get(VILLAGER_PF).get()));
        context.register(BEEDLE_STP, singleFeaturePool(emptyPool, pfLookup.get(BEEDLE_PF).get()));
        context.register(KASS_COMMON_STP, singleFeaturePoolWithChance(emptyPool, pfLookup.get(KASS_PF).get(), 3, 4));
        context.register(KASS_RARE_STP, singleFeaturePoolWithChance(emptyPool, pfLookup.get(KASS_PF).get(), 1, 4));
        context.register(HORSE_STP, singleFeaturePool(emptyPool, pfLookup.get(HORSE_PF).get()));
        context.register(DONKEY_STP, singleFeaturePool(emptyPool, pfLookup.get(DONKEY_PF).get()));

    }

    private static StructureTemplatePool singleFeaturePool(Holder<StructureTemplatePool> fallback, Holder<PlacedFeature> holder) {
        return new StructureTemplatePool(
                fallback,
                ImmutableList.of(
                        Pair.of(customFeature(holder), 1)),
                StructureTemplatePool.Projection.RIGID);
    }

    private static StructureTemplatePool singleFeaturePoolWithChance(Holder<StructureTemplatePool> fallback, Holder<PlacedFeature> holder, int weight, int total) {
        return new StructureTemplatePool(
                fallback,
                ImmutableList.of(
                        Pair.of(customFeature(holder), weight),
                        Pair.of(StructurePoolElement.empty(), total - weight)),
                StructureTemplatePool.Projection.RIGID);
    }

    public static Function<StructureTemplatePool.Projection, CustomFeaturePoolElement> customFeature(Holder<PlacedFeature> feature) {
        return (projection) -> new CustomFeaturePoolElement(feature, projection);
    }

    public static void loadClass() {
    }
}