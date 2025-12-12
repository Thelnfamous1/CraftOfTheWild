package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.StoneTalusModel;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.StoneTalus;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class StoneTalusRenderer<T extends StoneTalus> extends COTWMobRenderer<T> {
    public StoneTalusRenderer(EntityRendererProvider.Context renderManager, ResourceLocation id) {
        super(renderManager, new StoneTalusModel<>(id));
        this.withScale(StoneTalus.VISUAL_SCALE);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, T animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        this.shadowRadius = this.getShadowScale() * animatable.getBbWidth();
        widthScale *= animatable.getScale();
        heightScale *= animatable.getScale();
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    protected float getShadowScale() {
        return 0.5F;
    }
}
