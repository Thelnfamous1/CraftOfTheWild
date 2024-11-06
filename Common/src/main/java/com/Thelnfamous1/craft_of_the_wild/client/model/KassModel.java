package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.Kass;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class KassModel extends DefaultedEntityGeoModel<Kass> {

    public KassModel() {
        super(EntityInit.KASS.getId());
        withAltTexture(EntityInit.KASS.getId().withPath(p -> p + "/" + p));
    }
}
