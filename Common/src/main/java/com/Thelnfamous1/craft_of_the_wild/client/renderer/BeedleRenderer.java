package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.BeedleModel;
import com.Thelnfamous1.craft_of_the_wild.entity.Beedle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BeedleRenderer extends COTWMobRenderer<Beedle> {
    private static final float RENDER_SCALE = 13F / 15F;

    public BeedleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BeedleModel());
        this.withScale(RENDER_SCALE);
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){
            @Override
            public void render(PoseStack poseStack, Beedle animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if(animatable.isLightOn()){
                    super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
                }
            }
        });
    }
}
