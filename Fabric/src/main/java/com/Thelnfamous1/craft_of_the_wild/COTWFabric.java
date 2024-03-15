package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.WorldGenInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;

public class COTWFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        COTWCommon.init();
        EntityInit.attributeSuppliers.forEach(
                p -> FabricDefaultAttributeRegistry.register(p.entityTypeSupplier().get(), p.factory().get().build())
        );
        COTWCommon.registerSpawnEggs();
        SpawnPlacements.register(EntityInit.STONE_TALUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                StoneTalus::checkStoneTalusSpawnRules);
        /*
        BiomeModifications.addSpawn(BiomeSelectors.tag(COTWTags.SPAWNS_STONE_TALUS),
                MobCategory.MONSTER,
                EntityInit.STONE_TALUS.get(), 30, 1, 1);
         */
        BiomeModifications.addFeature(BiomeSelectors.tag(COTWTags.SPAWNS_STONE_TALUS),
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                WorldGenInit.STONE_TALUS_PF);
    }

}
