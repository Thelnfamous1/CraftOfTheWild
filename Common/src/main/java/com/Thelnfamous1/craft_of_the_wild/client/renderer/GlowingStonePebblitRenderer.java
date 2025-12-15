package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.entity.pebblit.StonePebblit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GlowingStonePebblitRenderer<T extends StonePebblit> extends StonePebblitRenderer<T> {

    public GlowingStonePebblitRenderer(EntityRendererProvider.Context renderManager, ResourceLocation id) {
        super(renderManager, id);
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
