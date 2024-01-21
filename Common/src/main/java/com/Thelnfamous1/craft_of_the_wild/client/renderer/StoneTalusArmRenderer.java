package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.StoneTalusArmModel;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusArm;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StoneTalusArmRenderer extends GeoEntityRenderer<StoneTalusArm> {

    private final StoneTalusArmModel armModel;

    public StoneTalusArmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StoneTalusArmModel());
        this.armModel = (StoneTalusArmModel)this.model;
        this.withScale(StoneTalus.SCALE);
    }

    @Override
    public void render(StoneTalusArm $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void preRender(PoseStack poseStack, StoneTalusArm animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, StoneTalusArm animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        this.shadowRadius = this.getShadowScale() * animatable.getBbWidth();
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    protected float getShadowScale() {
        return 0.5F;
    }
}
