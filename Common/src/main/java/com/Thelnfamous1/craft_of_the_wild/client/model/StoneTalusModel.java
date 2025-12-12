package com.Thelnfamous1.craft_of_the_wild.client.model;

import com.Thelnfamous1.craft_of_the_wild.entity.talus.StoneTalus;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.HashMap;
import java.util.Map;

public class StoneTalusModel<T extends StoneTalus> extends DefaultedEntityGeoModel<T> {
    private final Map<String, ResourceLocation> TEXTURE_PATHS = new HashMap<>();
    private final ResourceLocation id;

    public StoneTalusModel(ResourceLocation id) {
        super(id);
        this.id = id;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.TEXTURE_PATHS.computeIfAbsent(animatable.getVariant().getName(),
                k -> this.buildFormattedTexturePath(this.id
                        .withPath(path -> path + "/" + animatable.getVariant().getName())));
    }
}
