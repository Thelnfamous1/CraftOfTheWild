package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.pebblit.StonePebblit;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class StonePebblitModel<T extends StonePebblit> extends DefaultedEntityGeoModel<T> {

    public StonePebblitModel(ResourceLocation id) {
        super(id);
    }
}
