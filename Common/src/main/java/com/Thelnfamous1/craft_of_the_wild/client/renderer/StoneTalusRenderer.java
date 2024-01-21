package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.StoneTalusModel;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StoneTalusRenderer extends GeoEntityRenderer<StoneTalus> {
    public StoneTalusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StoneTalusModel());
        this.withScale(StoneTalus.SCALE);
    }

    @Override
    protected void applyRotations(StoneTalus animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        // Same logic as super, but removed all handling for Pose.SLEEPING as the Talus sleeps inside the ground

        poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));

        if (animatable.deathTime > 0) {
            float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;

            poseStack.mulPose(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
        }
        else if (animatable.isAutoSpinAttack()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f - animatable.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees((animatable.tickCount + partialTick) * -75f));
        }
        else if (animatable.hasCustomName()) {
            String name = animatable.getName().getString();
            name = ChatFormatting.stripFormatting(name);
            if (name != null && (name.equals("Dinnerbone") || name.equalsIgnoreCase("Grumm"))) {
                poseStack.translate(0, animatable.getBbHeight() + 0.1f, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
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
