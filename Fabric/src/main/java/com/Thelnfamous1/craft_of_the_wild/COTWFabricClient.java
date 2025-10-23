package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import com.Thelnfamous1.craft_of_the_wild.client.renderer.RadiantItemBEWLR;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.network.COTWFabricNetwork;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class COTWFabricClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        COTWCommonClient.init();
        COTWCommonClient.registerRenderers(EntityRendererRegistry::register);
        COTWFabricNetwork.registerClientPackets();
        COTWCommonClient.setup();
        COTWCommonClient.registerParticles((particleType, particleProvider) ->
                ParticleFactoryRegistry.getInstance().register(particleType, sprites -> particleProvider));
        COTWCommonClient.registerItemModelProperties(ItemProperties::register);
        /*
        BuiltinItemRendererRegistry.INSTANCE.register(ItemInit.LUMINOUS_STONE.get(), new BuiltinItemRendererRegistry.DynamicItemRenderer() {
            private RadiantItemBEWLR renderer = null;

            @Override
            public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
                if (this.renderer == null)
                    this.renderer = new RadiantItemBEWLR();

                this.renderer.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
            }
        });
         */
    }
}
