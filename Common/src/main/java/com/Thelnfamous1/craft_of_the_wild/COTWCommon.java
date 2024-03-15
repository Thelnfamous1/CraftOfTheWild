package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import com.Thelnfamous1.craft_of_the_wild.entity.BossMusicPlayer;
import com.Thelnfamous1.craft_of_the_wild.init.*;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import com.Thelnfamous1.craft_of_the_wild.mixin.SpawnEggItemAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class COTWCommon {

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        ItemInit.loadClass();
        BlockInit.loadClass();
        EntityInit.loadClass();
        SensorInit.loadClass();
        MemoryModuleInit.loadClass();
        DamageTypeInit.loadClass();
        AttributeInit.loadClass();
        SoundInit.loadClass();
        WorldGenInit.loadClass();
    }

    public static void registerSpawnEggs() {
        COTWSpawnEggItem.getEggs()
                .forEach(egg -> SpawnEggItemAccessor.craft_of_the_wild$getBY_ID().put(egg.type(), egg));
    }

    public static ResourceLocation getResourceLocation(String path) {
        return new ResourceLocation(Constants.MODID, path);
    }

    public static void debug(boolean debugFlag, String format, Object... args){
        if(debugFlag) Constants.LOG.info(format, args);
    }

    public static <T extends LivingEntity & BossMusicPlayer> void playBossMusicFor(T boss) {
        if(boss.level().isClientSide){
            COTWCommonClient.playBossMusicFor(boss);
        }
    }

    public static <T extends LivingEntity & BossMusicPlayer> void stopBossMusicFor(T boss) {
        if(boss.level().isClientSide){
            COTWCommonClient.stopBossMusicFor(boss);
        }
    }
}