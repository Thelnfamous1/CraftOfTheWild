package com.Thelnfamous1.craft_of_the_wild.client;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class COTWForgeClient {

    public static void init() {
        COTWCommonClient.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> COTWCommonClient.registerModelLayers(event::registerLayerDefinition));
        modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> COTWCommonClient.registerRenderers(event::registerEntityRenderer));
        modEventBus.addListener((EntityRenderersEvent.AddLayers event) -> {
            COTWCommonClient.addEntityRenderLayers((et) -> event.getRenderer((EntityType<? extends LivingEntity>) et), LivingEntityRenderer::addLayer, event.getContext());
        });
        modEventBus.addListener((RegisterColorHandlersEvent.Item event) -> COTWCommonClient.registerColorHandlers(event::register));
        modEventBus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(COTWCommonClient::setup);
            event.enqueueWork(() -> COTWCommonClient.registerItemModelProperties(ItemProperties::register));
        });
        modEventBus.addListener((RegisterParticleProvidersEvent event) -> COTWCommonClient.registerParticles(((particleType, particleProvider) ->
                event.registerSpriteSet(particleType, sprites -> particleProvider))));
        modEventBus.addListener((RegisterColorHandlersEvent.Item event) -> {
            COTWCommonClient.registerItemColors(event::register);
        });
    }

}
