package com.Thelnfamous1.craft_of_the_wild.plugin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.Map;

public record SeparateTransformsData(
        ResourceLocation baseModel, Map<ItemDisplayContext, ResourceLocation> perspectives
) {}