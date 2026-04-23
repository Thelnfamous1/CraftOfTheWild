package com.Thelnfamous1.craft_of_the_wild.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlternateArmorItem extends ArmorItem {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = new HashMap<>();

    public AlternateArmorItem(ArmorMaterial $$0, Type $$1, Properties $$2) {
        super($$0, $$1, $$2);
    }

    public String getArmorTexture(ItemStack stack, @Nullable String type){
        AlternateArmorItem item = (AlternateArmorItem)stack.getItem();
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

    public static ResourceLocation getArmorTextureResource(ItemStack stack, @Nullable String type){
        AlternateArmorItem item = (AlternateArmorItem)stack.getItem();
        String textureStr = item.getArmorTexture(stack, type);
        ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(textureStr);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(textureStr);
            ARMOR_LOCATION_CACHE.put(textureStr, resourcelocation);
        }

        return resourcelocation;
    }

    public static ItemStack createAlternate(ItemStack stack){
        ItemStack result = ItemStack.EMPTY;

        Item item = stack.getItem();
        if (item instanceof AlternateArmorItem) {
            result = stack.copyWithCount(1);
            setUseAlternate(result, usingAlternate(stack));
        }
        return result;
    }

    public static boolean usingAlternate(ItemStack stack){
        return stack.hasTag() && stack.getTag().getBoolean("UseAlternate");
    }

    public static void setUseAlternate(ItemStack stack, boolean useAlternate){
        stack.getOrCreateTag().putBoolean("UseAlternate", useAlternate);
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        super.appendHoverText($$0, $$1, $$2, $$3);
        if(usingAlternate($$0)){
            $$2.add(Component.translatable($$0.getDescriptionId() + ".alternate").withStyle(ChatFormatting.GRAY));
        }
    }
}
