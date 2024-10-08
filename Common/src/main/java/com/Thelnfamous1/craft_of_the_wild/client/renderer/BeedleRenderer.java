package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.BeedleModel;
import com.Thelnfamous1.craft_of_the_wild.entity.Beedle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BeedleRenderer extends GeoEntityRenderer<Beedle> {
    public BeedleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BeedleModel());
    }
}
