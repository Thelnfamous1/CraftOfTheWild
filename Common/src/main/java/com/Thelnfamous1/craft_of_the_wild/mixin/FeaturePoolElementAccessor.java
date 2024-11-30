package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FeaturePoolElement.class)
public interface FeaturePoolElementAccessor {

    @Accessor("feature")
    Holder<PlacedFeature> craft_of_the_wild$getFeature();

    @Accessor("defaultJigsawNBT")
    CompoundTag craft_of_the_wild$getDefaultJigsawNBT();
}
