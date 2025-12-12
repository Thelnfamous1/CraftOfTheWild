package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.StoneTalusArm;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.HashMap;
import java.util.Map;

public class StoneTalusArmModel<T extends StoneTalusArm> extends DefaultedEntityGeoModel<T> {
    private final Map<String, ResourceLocation> TEXTURE_PATHS = new HashMap<>();
    private final ResourceLocation id;

    public StoneTalusArmModel(ResourceLocation id) {
        super(id);
        this.id = id;
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        // for some reason the new model needs an additional 180 degrees of y rot and an additional 90 degrees of x rot
        float yRot = Mth.rotLerp(animationState.getPartialTick(), animatable.yRotO, animatable.getYRot()) + 180;
        float xRot = Mth.lerp(animationState.getPartialTick(), animatable.xRotO, animatable.getXRot()) + 90;
        this.rotateArm(yRot, xRot);
    }

    private void rotateArm(float yRot, float xRot) {
        CoreGeoBone arm = this.getAnimationProcessor().getBone("all");
        if(arm != null){
            arm.setRotY(-yRot * Mth.DEG_TO_RAD);
            arm.setRotX(-xRot * Mth.DEG_TO_RAD);
        }
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.TEXTURE_PATHS.computeIfAbsent(animatable.getVariant().getName(),
                k -> this.buildFormattedTexturePath(this.id
                        .withPath(path -> path + "/" + animatable.getVariant().getName())));
    }
}
