package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.entity.talus.StoneTalus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GlowingStoneTalusRenderer<T extends StoneTalus> extends StoneTalusRenderer<T>{
    public GlowingStoneTalusRenderer(EntityRendererProvider.Context renderManager, ResourceLocation id) {
        super(renderManager, id);
        this.addRenderLayer(new StoneTalusGlowingLayer<>(this, id));
    }
}
