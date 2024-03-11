package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import com.Thelnfamous1.craft_of_the_wild.network.COTWFabricNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class COTWFabricClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        COTWCommonClient.init();
        COTWCommonClient.registerRenderers(EntityRendererRegistry::register);
        COTWFabricNetwork.registerClientPackets();
    }
}
