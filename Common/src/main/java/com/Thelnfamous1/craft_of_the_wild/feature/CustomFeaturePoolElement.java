package com.Thelnfamous1.craft_of_the_wild.feature;

import com.Thelnfamous1.craft_of_the_wild.init.StructurePoolElementTypeInit;
import com.Thelnfamous1.craft_of_the_wild.mixin.FeaturePoolElementAccessor;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

public class CustomFeaturePoolElement extends FeaturePoolElement {

    public static final Codec<CustomFeaturePoolElement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(PlacedFeature.CODEC.fieldOf("feature").forGetter(($$0x) -> {
            return ((FeaturePoolElementAccessor)$$0x).craft_of_the_wild$getFeature();
        }), projectionCodec()).apply(instance, CustomFeaturePoolElement::new);
    });

    public CustomFeaturePoolElement(Holder<PlacedFeature> $$0, StructureTemplatePool.Projection $$1) {
        super($$0, $$1);
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager $$0, BlockPos blockPos, Rotation $$2, RandomSource $$3) {
        List<StructureTemplate.StructureBlockInfo> shuffled = Lists.newArrayList();
        for(FrontAndTop frontAndTop : FrontAndTop.values()){
            shuffled.add(new StructureTemplate.StructureBlockInfo(
                    blockPos,
                    Blocks.JIGSAW.defaultBlockState()
                            .setValue(JigsawBlock.ORIENTATION, frontAndTop),
                    ((FeaturePoolElementAccessor)this).craft_of_the_wild$getDefaultJigsawNBT()));
        }
        return shuffled;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementTypeInit.CUSTOM_FEATURE.get();
    }
}
