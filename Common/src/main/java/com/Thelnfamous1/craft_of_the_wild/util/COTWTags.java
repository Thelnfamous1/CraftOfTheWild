package com.Thelnfamous1.craft_of_the_wild.util;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class COTWTags {
    public final static TagKey<Block> STONE_TALUS_CAN_DESTROY = createBlockTag("stone_talus_can_destroy");
    public final static TagKey<Biome> SPAWNS_STONE_TALUS = createBiomeTag("spawns_stone_talus");
    public final static TagKey<Item> C418_MUSIC_DISCS = createItemTag("c418_music_discs");
    public static final TagKey<Biome> HAS_STABLES = createBiomeTag("has_stables");
    public static final TagKey<Biome> HAS_STABLES_SNOWY = createBiomeTag("has_stables_snowy");
    public static final TagKey<Block> STONE_TALUS_CAN_CONVERT_TO_DIRT = createBlockTag("stone_talus_can_convert_to_dirt");
    public static final TagKey<EntityType<?>> RADIANT_DISGUISE_AFFECTS = createEntityTypeTag("radiant_disguise_affects");
    public static final TagKey<EntityType<?>> BOKOLBIN_DISGUISE_AFFECTS = createEntityTypeTag("bokolbin_disguise_affects");
    public static final TagKey<EntityType<?>> STONE_TALUSES = createEntityTypeTag("stone_taluses");
    public static final TagKey<Item> BURN_IMMUNE_WEARABLES = createItemTag("burn_immune_wearables");
    public static final TagKey<EntityType<?>> STONE_PEBBLITS = createEntityTypeTag("stone_pebblits");
    public static final TagKey<EntityType<?>> STONE_TALUS_FRIENDS = createEntityTypeTag("stone_talus_friends");
    public static final TagKey<EntityType<?>> STONE_PEBBLIT_FRIENDS = createEntityTypeTag("stone_pebblit_friends");
    public static final TagKey<Fluid> STONE_PEBBLIT_DROWNS_IN = createFluidTypeTag("stone_pebblit_drowns_in");


    public static final TagKey<Biome> HAS_STONE_TALUS_BOULDERS = createBiomeTag("has_stone_talus_boulders");
    public static final TagKey<Biome> HAS_FROST_TALUS_BOULDERS = createBiomeTag("has_frost_talus_boulders");
    public static final TagKey<Biome> HAS_IGNEO_TALUS_BOULDERS = createBiomeTag("has_igneo_talus_boulders");

    public static final TagKey<Block> STONE_TALUS_BOULDER_COMPONENT = createBlockTag("stone_talus_boulder_component");
    public static final TagKey<Block> FROST_TALUS_BOULDER_COMPONENT = createBlockTag("frost_talus_boulder_component");
    public static final TagKey<Block> IGNEO_TALUS_BOULDER_COMPONENT = createBlockTag("igneo_talus_boulder_component");


    private static TagKey<Block> createBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, COTWCommon.getResourceLocation(path));
    }

    private static TagKey<Biome> createBiomeTag(String path) {
        return TagKey.create(Registries.BIOME, COTWCommon.getResourceLocation(path));
    }

    private static TagKey<Item> createItemTag(String path) {
        return TagKey.create(Registries.ITEM, COTWCommon.getResourceLocation(path));
    }

    private static TagKey<EntityType<?>> createEntityTypeTag(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, COTWCommon.getResourceLocation(path));
    }

    private static TagKey<Fluid> createFluidTypeTag(String path) {
        return TagKey.create(Registries.FLUID, COTWCommon.getResourceLocation(path));
    }
}
