package com.Thelnfamous1.craft_of_the_wild.client;

import com.Thelnfamous1.craft_of_the_wild.client.renderer.StoneTalusRenderer;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;

import java.util.function.BiConsumer;

public class COTWCommonClient {

    public static void registerRenderers(BiConsumer<EntityType, EntityRendererProvider> register){
        registerRendererTyped(EntityInit.STONE_TALUS.get(), StoneTalusRenderer::new, register);
    }

    private static <T extends Entity> void registerRendererTyped(EntityType<T> type, EntityRendererProvider<T> provider, BiConsumer<EntityType, EntityRendererProvider> register) {
        register.accept(type, provider);
    }

    public static void registerColorHandlers(BiConsumer<ItemColor, ItemLike> register) {
        COTWSpawnEggItem.getEggs().forEach(egg -> register.accept((stack, layer) -> egg.getColor(layer), egg));
    }
}
