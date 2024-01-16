package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.StoneTalusModel;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StoneTalusRenderer extends GeoEntityRenderer<StoneTalus> {
    public StoneTalusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StoneTalusModel());
    }
}