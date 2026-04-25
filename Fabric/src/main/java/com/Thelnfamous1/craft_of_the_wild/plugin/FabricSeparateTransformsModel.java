package com.Thelnfamous1.craft_of_the_wild.plugin;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FabricSeparateTransformsModel implements BakedModel, FabricBakedModel {
    private final boolean isAmbientOcclusion;
    private final boolean isGui3d;
    private final boolean isSideLit;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;
    private final BakedModel baseModel;
    private final Map<ItemDisplayContext, BakedModel> perspectives;
    private final ItemTransforms transforms;

    public FabricSeparateTransformsModel(BakedModel original, BakedModel baseModel, Map<ItemDisplayContext, BakedModel> perspectives){
        this(original.useAmbientOcclusion(), original.isGui3d(), original.usesBlockLight(), original.getParticleIcon(), original.getOverrides(), baseModel, perspectives);
    }

    public FabricSeparateTransformsModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, BakedModel baseModel, Map<ItemDisplayContext, BakedModel> perspectives) {
        this.isAmbientOcclusion = isAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.isSideLit = isSideLit;
        this.particle = particle;
        this.overrides = overrides;
        this.baseModel = baseModel;
        this.perspectives = perspectives;

        ItemTransform gui = perspectives.getOrDefault(ItemDisplayContext.GUI, baseModel)
                .getTransforms().getTransform(ItemDisplayContext.GUI);
        ItemTransform ground = perspectives.getOrDefault(ItemDisplayContext.GROUND, baseModel)
                .getTransforms().getTransform(ItemDisplayContext.GROUND);
        ItemTransform fixed = perspectives.getOrDefault(ItemDisplayContext.FIXED, baseModel)
                .getTransforms().getTransform(ItemDisplayContext.FIXED);
        ItemTransform thirdPersonRight = perspectives.getOrDefault(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, baseModel)
                .getTransforms().getTransform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        ItemTransform thirdPersonLeft = perspectives.getOrDefault(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, baseModel)
                .getTransforms().getTransform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        ItemTransform firstPersonRight = perspectives.getOrDefault(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, baseModel)
                .getTransforms().getTransform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        ItemTransform firstPersonLeft = perspectives.getOrDefault(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, baseModel)
                .getTransforms().getTransform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        ItemTransform head = perspectives.getOrDefault(ItemDisplayContext.HEAD, baseModel)
                .getTransforms().getTransform(ItemDisplayContext.HEAD);

        this.transforms = new ItemTransforms(thirdPersonLeft, thirdPersonRight, firstPersonLeft, firstPersonRight,
                head, gui, ground, fixed);

    }


    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var transformType = context.itemTransformationMode();
        BakedModel m = perspectives.getOrDefault(transformType, baseModel);
        VanillaModelEncoder.emitItemQuads(m, null, randomSupplier, context);
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var transformType = context.itemTransformationMode();
        BakedModel m = perspectives.getOrDefault(transformType, baseModel);
        VanillaModelEncoder.emitBlockQuads(
                m,
                state,
                randomSupplier,
                context,
                context.getEmitter()
        );
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return baseModel.getQuads(state, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return isAmbientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return isGui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return isSideLit;
    }


    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    @Override
    public ItemTransforms getTransforms() {
        return transforms;
    }

}