package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.HashMap;
import java.util.Map;

public class StoneTalusModel extends DefaultedEntityGeoModel<StoneTalus> {
    private final Map<String, ResourceLocation> TEXTURE_PATHS = new HashMap<>();

    public StoneTalusModel() {
        super(EntityInit.STONE_TALUS.getId());
    }

    @Override
    public ResourceLocation getTextureResource(StoneTalus animatable) {
        return this.TEXTURE_PATHS.computeIfAbsent(animatable.getVariant().getName(),
                k -> this.buildFormattedTexturePath(EntityInit.STONE_TALUS.getId()
                        .withPath(path -> path + "/" + animatable.getVariant().getName())));
    }
}
