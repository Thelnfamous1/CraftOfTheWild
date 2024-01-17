package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class StoneTalusModel extends DefaultedEntityGeoModel<StoneTalus> {
    private final ResourceLocation battleTexturePath;

    public StoneTalusModel() {
        super(EntityInit.STONE_TALUS.getId());
        this.withAltTexture(EntityInit.STONE_TALUS.getId().withPath(path -> path + "/normal"));
        this.battleTexturePath = this.buildFormattedTexturePath(EntityInit.STONE_TALUS.getId().withPath(path -> path + "/battle"));
    }

    @Override
    public ResourceLocation getTextureResource(StoneTalus animatable) {
        if(animatable.isBattle()){
            return battleTexturePath;
        }
        return super.getTextureResource(animatable);
    }
}
