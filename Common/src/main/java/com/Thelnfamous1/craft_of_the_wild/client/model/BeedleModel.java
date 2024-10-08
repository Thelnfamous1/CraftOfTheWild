package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.Beedle;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BeedleModel extends DefaultedEntityGeoModel<Beedle> {

    public BeedleModel() {
        super(EntityInit.BEEDLE.getId());
        withAltTexture(EntityInit.BEEDLE.getId().withPath(p -> p + "/" + p));
    }
}
