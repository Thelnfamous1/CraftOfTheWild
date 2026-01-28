package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.client.model.KiltonModel;
import com.Thelnfamous1.craft_of_the_wild.entity.trader.Kilton;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KiltonRenderer extends COTWMobRenderer<Kilton> {
    private static final float RENDER_SCALE = EntityInit.KILTON_SCALE;

    public KiltonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KiltonModel());
        this.withScale(RENDER_SCALE);
    }
}
