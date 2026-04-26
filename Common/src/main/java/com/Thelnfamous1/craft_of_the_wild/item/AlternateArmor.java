package com.Thelnfamous1.craft_of_the_wild.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public interface AlternateArmor {
    Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = new HashMap<>();

    static boolean usingAlternate(ItemStack stack){
        return stack.hasTag() && stack.getTag().getBoolean("UseAlternate");
    }

    static ItemStack createAlternate(ItemStack stack){
        ItemStack result = ItemStack.EMPTY;

        Item item = stack.getItem();
        if (item instanceof HylianAlternateArmorItem) {
            result = stack.copyWithCount(1);
            setUseAlternate(result, AlternateArmor.usingAlternate(stack));
        }
        return result;
    }

    static void setUseAlternate(ItemStack stack, boolean useAlternate){
        stack.getOrCreateTag().putBoolean("UseAlternate", useAlternate);
    }

    static ResourceLocation getArmorTextureResource(ItemStack stack, @Nullable String type){
        HylianAlternateArmorItem item = (HylianAlternateArmorItem)stack.getItem();
        String textureStr = item.getArmorTexture(stack, type);
        ResourceLocation resourcelocation = AlternateArmor.ARMOR_LOCATION_CACHE.get(textureStr);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(textureStr);
            AlternateArmor.ARMOR_LOCATION_CACHE.put(textureStr, resourcelocation);
        }

        return resourcelocation;
    }

    default String getArmorTexture(ItemStack stack, @Nullable String type){
        HylianAlternateArmorItem item = (HylianAlternateArmorItem)stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        boolean isHoodDown = usingAlternate(stack);
        return String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s%s.png",
                domain,
                texture,
                1,
                isHoodDown ? String.format(Locale.ROOT, "_%s", "alternate") : "",
                type == null ? "" : String.format(Locale.ROOT, "_%s", type));
    }
}
