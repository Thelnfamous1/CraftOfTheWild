package com.Thelnfamous1.craft_of_the_wild.block;

import com.Thelnfamous1.craft_of_the_wild.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

public class StoneTalusBoulderBlock extends BaseEntityBlock {
    private final ResourceKey<Structure> stoneTalusBoulderStructure;
    private final TagKey<Block> stoneTalusBoulderComponents;

    public StoneTalusBoulderBlock(Properties $$0, ResourceKey<Structure> stoneTalusBoulderStructure, TagKey<Block> stoneTalusBoulderComponents) {
        super($$0);
        this.stoneTalusBoulderStructure = stoneTalusBoulderStructure;
        this.stoneTalusBoulderComponents = stoneTalusBoulderComponents;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        StoneTalusBoulderBlockEntity stoneTalusBoulderBlockEntity = new StoneTalusBoulderBlockEntity(blockPos, blockState);
        stoneTalusBoulderBlockEntity.setStoneTalusBoulderInfo(this.stoneTalusBoulderStructure, this.stoneTalusBoulderComponents);
        return stoneTalusBoulderBlockEntity;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return !$$0.isClientSide ? createTickerHelper($$2, BlockInit.STONE_TALUS_BOULDER_BE.get(), StoneTalusBoulderBlockEntity::tickEntity) : null;
    }


}
