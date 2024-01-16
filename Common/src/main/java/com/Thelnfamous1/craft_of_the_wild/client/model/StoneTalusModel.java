package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class StoneTalusModel extends DefaultedEntityGeoModel<StoneTalus> {
    public StoneTalusModel() {
        super(EntityInit.STONE_TALUS.getId());
    }
}
