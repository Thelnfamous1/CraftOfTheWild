package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.KassModel;
import com.Thelnfamous1.craft_of_the_wild.entity.Kass;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KassRenderer extends COTWMobRenderer<Kass> {

    public KassRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KassModel());
    }
}
