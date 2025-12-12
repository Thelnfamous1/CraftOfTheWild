package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.StoneTalusArm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GlowingStoneTalusArmRenderer<T extends StoneTalusArm> extends StoneTalusArmRenderer<T> {

    public GlowingStoneTalusArmRenderer(EntityRendererProvider.Context renderManager, ResourceLocation id) {
        super(renderManager, id);
        this.addRenderLayer(new StoneTalusArmGlowingLayer<>(this, id));
    }
}
