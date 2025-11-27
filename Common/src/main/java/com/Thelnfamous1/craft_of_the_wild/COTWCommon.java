package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import com.Thelnfamous1.craft_of_the_wild.entity.CustomMusicPlayer;
import com.Thelnfamous1.craft_of_the_wild.init.*;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import com.Thelnfamous1.craft_of_the_wild.mixin.SpawnEggItemAccessor;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

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
        PaintingVariantInit.loadClass();
        StructurePoolElementTypeInit.loadClass();
        ParticleInit.loadClass();
        MobEffectInit.loadClass();
        CriterionInit.registerAll();
        VillagerProfessionInit.loadClass();
    }

    public static void registerSpawnEggs() {
        COTWSpawnEggItem.getEggs()
                .forEach(egg -> SpawnEggItemAccessor.craft_of_the_wild$getBY_ID().put(egg.type(), egg));
    }

    public static ResourceLocation getResourceLocation(String path) {
        return new ResourceLocation(Constants.MODID, path);
    }

    public static String makeDescriptionId(String prefix, String path) {
        return Util.makeDescriptionId(prefix, getResourceLocation(path));
    }

    public static void debug(boolean debugFlag, String format, Object... args){
        if(debugFlag) Constants.LOG.info(format, args);
    }

    public static <T extends LivingEntity & CustomMusicPlayer> void playCustomMusicFor(T boss) {
        if(boss.level().isClientSide){
            COTWCommonClient.playCustomMusicFor(boss);
        }
    }

    public static <T extends LivingEntity & CustomMusicPlayer> void stopCustomMusicFor(T boss) {
        if(boss.level().isClientSide){
            COTWCommonClient.stopCustomMusicFor(boss);
        }
    }

    public static boolean isUnableToTarget(LivingEntity attacker, LivingEntity target){
        // If the target attacked the attacker, allow retaliation
        if(attacker.getLastHurtByMob() == target || attacker.getBrain().isMemoryValue(MemoryModuleType.ANGRY_AT, target.getUUID())){
            return false;
        }
        if(attacker.getType().is(COTWTags.RADIANT_DISGUISE_AFFECTS) && target.hasEffect(MobEffectInit.RADIANT_DISGUISE.get())){
            return true;
        } else if(attacker.getType().is(COTWTags.BOKOLBIN_DISGUISE_AFFECTS) && target.hasEffect(MobEffectInit.BOKOBLIN_DISGUISE.get())){
            return true;
        }
        return false;
    }

    public static boolean isMedalOfHonorTalusEquipped(LivingEntity entity) {
        return entity.getOffhandItem().is(ItemInit.MEDAL_OF_HONOR_TALUS.get()) || Services.PLATFORM.hasCharmEquipped(entity, ItemInit.MEDAL_OF_HONOR_TALUS.get(), "charm");
    }
}