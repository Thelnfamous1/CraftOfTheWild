package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.block.MonsterCakeBlock;
import com.Thelnfamous1.craft_of_the_wild.init.BlockInit;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
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
        copyParent(BlockInit.STONE_TALUS_BOULDER, mcLoc("block/stone"));
        copyParent(BlockInit.FROST_TALUS_BOULDER, mcLoc("block/blue_ice"));
        copyParent(BlockInit.IGNEO_TALUS_BOULDER, mcLoc("block/magma_block"));
        copyParent(BlockInit.STONE_PEBBLIT_BOULDER, mcLoc("block/stone"));
        copyParent(BlockInit.FROST_PEBBLIT_BOULDER, mcLoc("block/blue_ice"));
        copyParent(BlockInit.IGNEO_PEBBLIT_BOULDER, mcLoc("block/magma_block"));
        this.cakeLikeBlock(BlockInit.MONSTER_CAKE);

    }

    private void cakeLikeBlock(RegistryObject<Block> blockRegistryObject) {
        VariantBlockStateBuilder builder = getVariantBuilder(blockRegistryObject.get());

        for (int bites = 0; bites <= MonsterCakeBlock.MAX_BITES; bites++) {
            ResourceLocation modelLoc = blockRegistryObject.getId().withPrefix("block/").withSuffix(bites == 0 ? "" : "_slice" + bites);

            builder.partialState()
                    .with(MonsterCakeBlock.BITES, bites)
                    .modelForState()
                    .modelFile(models().getExistingFile(modelLoc))
                    .addModel();
        }
    }

    private void copyParent(RegistryObject<Block> blockRegistryObject, ResourceLocation parent) {
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
