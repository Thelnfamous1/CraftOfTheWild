package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.init.BlockInit;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, Constants.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        // Stream.of(
        //
        //         )
        //         .map(Supplier::get)
        //         .forEach(this::simpleCubeBottomTopBlockState);
        //
        // Stream.of(
        //
        // ).map(Supplier::get)
        //         .forEach(this::simpleBlock);
        stoneTalusBoulder(BlockInit.STONE_TALUS_BOULDER, mcLoc("block/stone"));
        stoneTalusBoulder(BlockInit.FROST_TALUS_BOULDER, mcLoc("block/blue_ice"));
        stoneTalusBoulder(BlockInit.IGNEO_TALUS_BOULDER, mcLoc("block/magma_block"));

    }

    private void stoneTalusBoulder(RegistryObject<Block> blockRegistryObject, ResourceLocation parent) {
        BlockModelBuilder stoneTalusBoulder = this.models().withExistingParent(blockRegistryObject.getId().getPath(), parent);
        this.simpleBlockWithItem(blockRegistryObject.get(), stoneTalusBoulder);
    }

    protected void simpleCubeBottomTopBlockState(Block block) {
        simpleBlock(block, blockCubeTopModel(block));
    }

    protected BlockModelBuilder blockCubeTopModel(Block block) {
        String name = getName(block);
        return models().cubeBottomTop(name, modLoc("block/" + name + "_side"), modLoc("block/" + name + "_bottom"), modLoc("block/" + name + "_top"));
    }

    protected String getName(Block item) {
        return ForgeRegistries.BLOCKS.getKey(item).getPath();
    }
}
