package com.Thelnfamous1.craft_of_the_wild.util;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class COTWTags {
    public final static TagKey<Block> STONE_TALUS_CAN_DESTROY = createBlockTag("stone_talus_can_destroy");
    public final static TagKey<Biome> SPAWNS_STONE_TALUS = createBiomeTag("spawns_stone_talus");

    private static TagKey<Block> createBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, COTWCommon.getResourceLocation(path));
    }

    private static TagKey<Biome> createBiomeTag(String path) {
        return TagKey.create(Registries.BIOME, COTWCommon.getResourceLocation(path));
    }
}
