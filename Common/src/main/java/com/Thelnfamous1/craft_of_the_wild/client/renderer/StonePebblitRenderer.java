package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.StonePebblitModel;
import com.Thelnfamous1.craft_of_the_wild.entity.pebblit.StonePebblit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class StonePebblitRenderer<T extends StonePebblit> extends COTWMobRenderer<T> {
    private final BlockRenderDispatcher dispatcher;
    public StonePebblitRenderer(EntityRendererProvider.Context renderManager, ResourceLocation id) {
        super(renderManager, new StonePebblitModel<>(id));
        this.withScale(StonePebblit.VISUAL_SCALE);
        this.dispatcher = renderManager.getBlockRenderDispatcher();
    }

    @Override
    public void defaultRender(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        BlockState disguiseBlockState = animatable.getDisguiseBlockState();
        if(!disguiseBlockState.isAir()){
            poseStack.pushPose();
            poseStack.translate(-0.5D, 0.0D, -0.5D);
            this.dispatcher.renderSingleBlock(disguiseBlockState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        } else{
            super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        }
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, T animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        if(!animatable.hasBlockDisguise()){
            this.shadowRadius = this.getShadowScale() * animatable.getBbWidth();
            widthScale *= animatable.getScale();
            heightScale *= animatable.getScale();
            super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
        }
    }

    protected float getShadowScale() {
        return 0.5F;
    }
}
