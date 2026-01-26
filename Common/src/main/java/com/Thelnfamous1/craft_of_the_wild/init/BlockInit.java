package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.block.StonePebblitBoulderBlockEntity;
import com.Thelnfamous1.craft_of_the_wild.block.StoneTalusBoulderBlock;
import com.Thelnfamous1.craft_of_the_wild.block.StoneTalusBoulderBlockEntity;
import com.Thelnfamous1.craft_of_the_wild.block.StoneTalusPebblitBlock;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.mojang.datafixers.types.Type;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockInit {
    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, Constants.MODID);
    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, Constants.MODID);

    public static final RegistryObject<Block> STONE_TALUS_BOULDER = BLOCKS.register("stone_talus_boulder", () -> new StoneTalusBoulderBlock(BlockBehaviour.Properties.copy(Blocks.STONE), WorldGenInit.STONE_TALUS_BOULDER_STRUCTURE, COTWTags.STONE_TALUS_BOULDER_COMPONENT, COTWTags.STONE_TALUS_BOULDER_SPAWNS));
    public static final RegistryObject<Block> FROST_TALUS_BOULDER = BLOCKS.register("frost_talus_boulder", () -> new StoneTalusBoulderBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_ICE), WorldGenInit.FROST_TALUS_BOULDER_STRUCTURE, COTWTags.FROST_TALUS_BOULDER_COMPONENT, COTWTags.FROST_TALUS_BOULDER_SPAWNS));
    public static final RegistryObject<Block> IGNEO_TALUS_BOULDER = BLOCKS.register("igneo_talus_boulder", () -> new StoneTalusBoulderBlock(BlockBehaviour.Properties.copy(Blocks.MAGMA_BLOCK), WorldGenInit.IGNEO_TALUS_BOULDER_STRUCTURE, COTWTags.IGNEO_TALUS_BOULDER_COMPONENT, COTWTags.IGNEO_TALUS_BOULDER_SPAWNS));

    public static final RegistryObject<Block> STONE_PEBBLIT_BOULDER = BLOCKS.register("stone_pebblit_boulder", () -> new StoneTalusPebblitBlock(BlockBehaviour.Properties.copy(Blocks.STONE), COTWTags.STONE_PEBBLIT_BOULDER_SPAWNS));
    public static final RegistryObject<Block> FROST_PEBBLIT_BOULDER = BLOCKS.register("frost_pebblit_boulder", () -> new StoneTalusPebblitBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_ICE), COTWTags.FROST_PEBBLIT_BOULDER_SPAWNS));
    public static final RegistryObject<Block> IGNEO_PEBBLIT_BOULDER = BLOCKS.register("igneo_pebblit_boulder", () -> new StoneTalusPebblitBlock(BlockBehaviour.Properties.copy(Blocks.MAGMA_BLOCK), COTWTags.IGNEO_PEBBLIT_BOULDER_SPAWNS));

    public static final RegistryObject<BlockEntityType<StoneTalusBoulderBlockEntity>> STONE_TALUS_BOULDER_BE = BLOCK_ENTITIES.register("stone_talus_boulder",
            () -> BlockEntityType.Builder.of(StoneTalusBoulderBlockEntity::new, STONE_TALUS_BOULDER.get(), FROST_TALUS_BOULDER.get(), IGNEO_TALUS_BOULDER.get())
                    .build(getBlockEntityTypeFixer("stone_talus_boulder")));
    public static final RegistryObject<BlockEntityType<StonePebblitBoulderBlockEntity>> STONE_PEBBLIT_BOULDER_BE = BLOCK_ENTITIES.register("stone_pebblit_boulder",
            () -> BlockEntityType.Builder.of(StonePebblitBoulderBlockEntity::new, STONE_PEBBLIT_BOULDER.get(), FROST_PEBBLIT_BOULDER.get(), IGNEO_PEBBLIT_BOULDER.get())
                    .build(getBlockEntityTypeFixer("stone_pebblit_boulder")));

    private static Type<?> getBlockEntityTypeFixer(String path) {
        return Util.fetchChoiceType(References.BLOCK_ENTITY, COTWCommon.getResourceLocation(path).toString());
    }

    public static void loadClass() {
    }
}
