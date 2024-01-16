package com.Thelnfamous1.craft_of_the_wild.client;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class COTWForgeClient {

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> COTWCommonClient.registerRenderers(event::registerEntityRenderer));
    }
}
