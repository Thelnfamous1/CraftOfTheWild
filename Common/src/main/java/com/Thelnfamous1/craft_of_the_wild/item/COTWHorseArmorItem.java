package com.Thelnfamous1.craft_of_the_wild.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HorseArmorItem;

public class COTWHorseArmorItem extends HorseArmorItem {
    private final ResourceLocation texture;
    public COTWHorseArmorItem(int $$0, ResourceLocation texture, Properties $$2) {
        super($$0, "", $$2);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTexture() {
        return this.texture;
    }
}
