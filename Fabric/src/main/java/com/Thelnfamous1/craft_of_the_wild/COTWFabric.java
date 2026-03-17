package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.duck.SaddleEquipper;
import com.Thelnfamous1.craft_of_the_wild.entity.trader.Beedle;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.*;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

public class COTWFabric implements ModInitializer {
    private static MinecraftServer currentServer = null;

    @Override
    public void onInitialize() {
        COTWCommon.init();
        EntityInit.attributeSuppliers.forEach(
                p -> FabricDefaultAttributeRegistry.register(p.entityTypeSupplier().get(), p.factory().get().build())
        );
        COTWCommon.registerSpawnEggs();
        SpawnPlacements.register(EntityInit.STONE_TALUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                StoneTalus::checkStoneTalusSpawnRules);
        SpawnPlacements.register(EntityInit.FROST_TALUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FrostTalus::checkFrostTalusSpawnRules);
        SpawnPlacements.register(EntityInit.IGNEO_TALUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                IgneoTalus::checkIgneoTalusSpawnRules);
        SpawnPlacements.register(EntityInit.STONE_TALUS_RARE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                StoneTalusRare::checkRareTalusSpawnRules);
        SpawnPlacements.register(EntityInit.STONE_TALUS_LUMINOUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                StoneTalusLuminous::checkLuminousTalusSpawnRules);
        /*
        BiomeModifications.addSpawn(BiomeSelectors.tag(COTWTags.SPAWNS_STONE_TALUS),
                MobCategory.MONSTER,
                EntityInit.STONE_TALUS.get(), 30, 1, 1);
         */
        /*
        BiomeModifications.addFeature(BiomeSelectors.tag(COTWTags.SPAWNS_STONE_TALUS),
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                WorldGenInit.STONE_TALUS_PF);
         */
        EntitySleepEvents.ALLOW_BED.register(((entity, sleepingPos, state, vanillaResult) -> {
            if(entity instanceof Beedle){
                return InteractionResult.SUCCESS;
            }
            return vanillaResult ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }));
        ServerLifecycleEvents.SERVER_STARTING.register(s -> currentServer = s);
        ServerLifecycleEvents.SERVER_STOPPED.register(s -> currentServer = null);
        EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {
            if(trackedEntity instanceof SaddleEquipper saddleEquipper){
                Services.PLATFORM.sendSyncSaddlePacket(player, trackedEntity, saddleEquipper.craft_of_the_wild$getSaddle());
            }
        });
    }

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }
}
