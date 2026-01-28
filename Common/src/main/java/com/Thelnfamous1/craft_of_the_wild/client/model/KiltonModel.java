package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.trader.Kilton;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class KiltonModel extends DefaultedEntityGeoModel<Kilton> {

    public KiltonModel() {
        super(EntityInit.KILTON.getId());
        withAltTexture(EntityInit.KILTON.getId().withPath(p -> p + "/" + p));
    }
}
