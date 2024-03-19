package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.client.COTWForgeClient;
import com.Thelnfamous1.craft_of_the_wild.datagen.*;
import com.Thelnfamous1.craft_of_the_wild.init.DamageTypeInit;
import com.Thelnfamous1.craft_of_the_wild.init.WorldGenInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Mod(Constants.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class COTWForge {
    
    public COTWForge() {
        Constants.LOG.info("Hello Forge world!");
        COTWCommon.init();
        if(FMLEnvironment.dist == Dist.CLIENT){
            COTWForgeClient.init();
        }
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();
        generator.addProvider(includeServer, new ModRecipeProvider(packOutput));
        generator.addProvider(includeServer, new ModLootTableProvider(packOutput));
        generator.addProvider(includeServer, new ModSoundProvider(packOutput, existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.Blocks(packOutput,event.getLookupProvider(), existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.Items(packOutput,event.getLookupProvider(), existingFileHelper));
        generator.addProvider(includeClient, new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(includeClient, new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(includeClient, new ModLangProvider(packOutput));
        generator.addProvider(includeServer, new ModTagProvider.COTWBiomeTags(packOutput, event.getLookupProvider(), existingFileHelper));
        RegistrySetBuilder builder = createRegistrySetBuilder();
        CompletableFuture<HolderLookup.Provider> registries = getRegistries(builder);
        generator.addProvider(includeServer, new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(), builder, Set.of(Constants.MODID)));
        generator.addProvider(includeServer, new ModTagProvider.DamageTypes(packOutput,registries, existingFileHelper));
    }

    private static RegistrySetBuilder createRegistrySetBuilder() {
        RegistrySetBuilder builder = registrySetBuilder();
        builder
                .add(Registries.DAMAGE_TYPE, DamageTypeInit::bootstrap)
                .add(Registries.PLACED_FEATURE, WorldGenInit::placedFeatures)
                .add(Registries.CONFIGURED_FEATURE, WorldGenInit::configuredFeature)
                /*
                .add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                    HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
                    context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, COTWCommon.getResourceLocation("stone_talus_spawns")),
                            ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                                    biomeLookup.getOrThrow(COTWTags.SPAWNS_STONE_TALUS),
                                    new MobSpawnSettings.SpawnerData(EntityInit.STONE_TALUS.get(), 30, 1, 1)));
                })
                 */
                .add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                    HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
                    HolderGetter<PlacedFeature> placedFeatureLookup = context.lookup(Registries.PLACED_FEATURE);
                    context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, COTWCommon.getResourceLocation("stone_talus_spawns")),
                            new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                                    biomeLookup.getOrThrow(COTWTags.SPAWNS_STONE_TALUS),
                                    HolderSet.direct(placedFeatureLookup.getOrThrow(WorldGenInit.STONE_TALUS_PF)),
                                    GenerationStep.Decoration.SURFACE_STRUCTURES
                                    ));
                });
                //.add(Registries.STRUCTURE, WorldGenInit::structures)
                //.add(Registries.STRUCTURE_SET, WorldGenInit::structureSets);
        return builder;
    }

    // Got this from Commoble
    private static RegistrySetBuilder registrySetBuilder() {
        return new RegistrySetBuilder() {
            @Override
            public HolderLookup.Provider build(RegistryAccess pRegistryAccess) {
                RegistrySetBuilder.BuildState registrysetbuilder$buildstate = this.createState(pRegistryAccess);
                Stream<HolderLookup.RegistryLookup<?>> stream = pRegistryAccess.registries().map((re) -> re.value().asLookup());
                Stream<HolderLookup.RegistryLookup<?>> stream1 = this.entries.stream().map((rs) -> rs.collectChanges(registrysetbuilder$buildstate).buildAsLookup());
                HolderLookup.Provider holderlookup$provider = HolderLookup.Provider.create(Stream.concat(stream, stream1.peek(registrysetbuilder$buildstate::addOwner)));
                // don't validate missing holder values
                // registrysetbuilder$buildstate.reportRemainingUnreferencedValues();
                registrysetbuilder$buildstate.throwOnError();
                return holderlookup$provider;
            }

            @Override
            public HolderLookup.Provider buildPatch(RegistryAccess registries, HolderLookup.Provider lookup) {
                RegistrySetBuilder.BuildState state = this.createState(registries);
                Map<ResourceKey<? extends Registry<?>>, RegistryContents<?>> map = new HashMap<>();
                state.collectReferencedRegistries().forEach((rc) -> map.put(rc.key(), rc));
                this.entries.stream().map((RegistryStub<?> stub) -> stub.collectChanges(state)).forEach((contents) -> map.put(contents.key(), contents));
                Stream<HolderLookup.RegistryLookup<?>> stream = registries.registries().map((entry) -> entry.value().asLookup());
                HolderLookup.Provider holderlookup$provider = HolderLookup.Provider
                        .create(Stream.concat(stream, map.values().stream().map(RegistrySetBuilder.RegistryContents::buildAsLookup).peek(state::addOwner)));
                state.fillMissingHolders(lookup);
                // don't validate missing holder values
                //registrysetbuilder$buildstate.reportRemainingUnreferencedValues();
                state.throwOnError();
                return holderlookup$provider;
            }
        };
    }

    private static CompletableFuture<HolderLookup.Provider> getRegistries(RegistrySetBuilder builder) {
        return CompletableFuture.supplyAsync(() -> COTWUtil.createLookup(builder), Util.backgroundExecutor());
    }

}