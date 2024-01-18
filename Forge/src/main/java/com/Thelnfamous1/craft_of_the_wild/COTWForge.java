package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.client.COTWForgeClient;
import com.Thelnfamous1.craft_of_the_wild.datagen.*;
import com.Thelnfamous1.craft_of_the_wild.init.DamageTypeInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
        RegistrySetBuilder builder = createRegistrySetBuilder();
        CompletableFuture<HolderLookup.Provider> registries = getRegistries(builder);
        generator.addProvider(includeServer, new DatapackBuiltinEntriesProvider(packOutput, registries, Set.of(Constants.MODID)));
        generator.addProvider(includeServer, new ModTagProvider.DamageTypes(packOutput,registries, existingFileHelper));
    }

    private static RegistrySetBuilder createRegistrySetBuilder() {
        RegistrySetBuilder builder = new RegistrySetBuilder();
        builder.add(Registries.DAMAGE_TYPE, DamageTypeInit::bootstrap);
        return builder;
    }

    private static CompletableFuture<HolderLookup.Provider> getRegistries(RegistrySetBuilder builder) {
        return CompletableFuture.supplyAsync(() -> COTWUtil.createLookup(builder), Util.backgroundExecutor());
    }

}