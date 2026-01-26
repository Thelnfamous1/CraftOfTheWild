package com.Thelnfamous1.craft_of_the_wild.block;

import com.Thelnfamous1.craft_of_the_wild.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StoneTalusPebblitBlock extends BaseEntityBlock {
    private final TagKey<EntityType<?>> stonePebblitBoulderSpawns;

    public StoneTalusPebblitBlock(Properties $$0, TagKey<EntityType<?>> stonePebblitBoulderSpawns) {
        super($$0);
        this.stonePebblitBoulderSpawns = stonePebblitBoulderSpawns;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        StonePebblitBoulderBlockEntity stonePebblitBoulderBlockEntity = new StonePebblitBoulderBlockEntity(blockPos, blockState);
        stonePebblitBoulderBlockEntity.setStonePebblitBoulderInfo(this.stonePebblitBoulderSpawns);
        return stonePebblitBoulderBlockEntity;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return !$$0.isClientSide ? createTickerHelper($$2, BlockInit.STONE_PEBBLIT_BOULDER_BE.get(), StonePebblitBoulderBlockEntity::tickEntity) : null;
    }


}
