package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.StoneTalusModel;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StoneTalusRenderer extends GeoEntityRenderer<StoneTalus> {
    public StoneTalusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StoneTalusModel());
        this.withScale(StoneTalus.SCALE);
    }

    @Override
    protected void applyRotations(StoneTalus animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        if(!animatable.hasPose(Pose.SLEEPING)){
            super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        }
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, StoneTalus animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        this.shadowRadius = this.getShadowScale() * animatable.getBbWidth();
        widthScale *= animatable.getScale();
        heightScale *= animatable.getScale();
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    protected float getShadowScale() {
        return 0.5F;
    }
}
