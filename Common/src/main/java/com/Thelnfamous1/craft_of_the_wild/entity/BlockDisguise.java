package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.world.level.block.state.BlockState;

public interface BlockDisguise {
    BlockState getDisguiseBlockState();

    default boolean hasBlockDisguise(){
        return !this.getDisguiseBlockState().isAir();
    }
}
