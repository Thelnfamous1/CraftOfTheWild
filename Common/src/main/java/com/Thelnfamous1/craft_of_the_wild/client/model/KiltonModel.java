package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.entity.trader.Kilton;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class KiltonModel extends DefaultedEntityGeoModel<Kilton> {
    private static final ResourceLocation KOLTIN_TEXTURE = COTWCommon.getResourceLocation("textures/entity/kilton/koltin.png");

    public KiltonModel() {
        super(EntityInit.KILTON.getId());
        withAltTexture(EntityInit.KILTON.getId().withPath(p -> p + "/" + p));
    }

    @Override
    public ResourceLocation getTextureResource(Kilton animatable) {
        if (animatable.hasCustomName()) {
            String name = animatable.getName().getString();
            name = ChatFormatting.stripFormatting(name);
            if (name != null && name.equals("Koltin")) {
                return KOLTIN_TEXTURE;
            }
        }
        return super.getTextureResource(animatable);
    }
}
