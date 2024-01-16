package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.client.COTWForgeClient;
import com.Thelnfamous1.craft_of_the_wild.datagen.ModBlockStateProvider;
import com.Thelnfamous1.craft_of_the_wild.datagen.ModItemModelProvider;
import com.Thelnfamous1.craft_of_the_wild.datagen.ModLangProvider;
import com.Thelnfamous1.craft_of_the_wild.datagen.ModTagProvider;
import com.Thelnfamous1.craft_of_the_wild.datagen.ModLootTableProvider;
import com.Thelnfamous1.craft_of_the_wild.datagen.ModRecipeProvider;
import com.Thelnfamous1.craft_of_the_wild.datagen.ModSoundProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
    }
}