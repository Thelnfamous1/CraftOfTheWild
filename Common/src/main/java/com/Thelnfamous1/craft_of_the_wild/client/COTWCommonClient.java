package com.Thelnfamous1.craft_of_the_wild.client;

import com.Thelnfamous1.craft_of_the_wild.client.renderer.StoneTalusRenderer;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.BiConsumer;

public class COTWCommonClient {

    public static void registerRenderers(BiConsumer<EntityType, EntityRendererProvider> register){
        registerRendererTyped(EntityInit.STONE_TALUS.get(), StoneTalusRenderer::new, register);
    }

    private static <T extends Entity> void registerRendererTyped(EntityType<T> type, EntityRendererProvider<T> provider, BiConsumer<EntityType, EntityRendererProvider> register) {
        register.accept(type, provider);
    }

}
