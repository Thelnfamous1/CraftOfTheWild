package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusArm;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class StoneTalusArmModel extends DefaultedEntityGeoModel<StoneTalusArm> {
    private final ResourceLocation battleTexturePath;

    public StoneTalusArmModel() {
        super(EntityInit.STONE_TALUS_ARM.getId());
        this.withAltTexture(EntityInit.STONE_TALUS_ARM.getId().withPath(path -> path + "/normal"));
        this.battleTexturePath = this.buildFormattedTexturePath(EntityInit.STONE_TALUS_ARM.getId().withPath(path -> path + "/battle"));
    }

    @Override
    public void setCustomAnimations(StoneTalusArm animatable, long instanceId, AnimationState<StoneTalusArm> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        float yRot = Mth.rotLerp(animationState.getPartialTick(), animatable.yRotO, animatable.getYRot()) - 90;
        float xRot = Mth.lerp(animationState.getPartialTick(), animatable.xRotO, animatable.getXRot());
        this.rotateArm(yRot, xRot);
    }

    private void rotateArm(float yRot, float xRot) {
        CoreGeoBone arm = getAnimationProcessor().getBone("arm_left");
        if(arm != null){
            arm.setRotY(-yRot * Mth.DEG_TO_RAD);
            arm.setRotX(-xRot * Mth.DEG_TO_RAD);
        }
    }

    @Override
    public ResourceLocation getTextureResource(StoneTalusArm animatable) {
        if(animatable.isBattle()){
            return battleTexturePath;
        }
        return super.getTextureResource(animatable);
    }
}
