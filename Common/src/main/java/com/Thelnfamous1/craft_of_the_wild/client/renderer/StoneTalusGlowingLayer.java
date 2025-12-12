package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.entity.talus.StoneTalus;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.HashMap;
import java.util.Map;

public class StoneTalusGlowingLayer<T extends StoneTalus> extends AutoGlowingGeoLayer<T> {
    private final Map<String, ResourceLocation> TEXTURE_PATHS = new HashMap<>();
    private final ResourceLocation id;

    public StoneTalusGlowingLayer(GeoRenderer<T> renderer, ResourceLocation id) {
        super(renderer);
        this.id = id;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.TEXTURE_PATHS.computeIfAbsent(animatable.getVariant().getName(),
                k -> this.buildFormattedTexturePath(this.id
                        .withPath(path -> path + "/" + animatable.getVariant().getName())));
    }

    public ResourceLocation buildFormattedTexturePath(ResourceLocation basePath) {
        return new ResourceLocation(basePath.getNamespace(), "textures/entity/" + basePath.getPath() + ".png");
    }
}
