package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.StoneTalusArmModel;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusArm;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StoneTalusArmRenderer extends GeoEntityRenderer<StoneTalusArm> {

    public StoneTalusArmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StoneTalusArmModel());
        this.withScale(StoneTalus.VISUAL_SCALE);
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
