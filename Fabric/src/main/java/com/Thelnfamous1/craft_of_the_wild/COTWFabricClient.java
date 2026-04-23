package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.item.AlternateArmorItem;
import com.Thelnfamous1.craft_of_the_wild.network.COTWFabricNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class COTWFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        COTWCommonClient.init();
        COTWCommonClient.registerRenderers(EntityRendererRegistry::register);
        COTWCommonClient.registerModelLayers((location, layerDefinition) -> EntityModelLayerRegistry.registerModelLayer(location, layerDefinition::get));
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            COTWCommonClient.addEntityRenderLayers(et -> et == entityType ? entityRenderer : null, ((LivingEntityRenderer renderer, RenderLayer layer) -> registrationHelper.register(layer)), context);
        });
        COTWFabricNetwork.registerClientPackets();
        COTWCommonClient.setup();
        COTWCommonClient.registerParticles((particleType, particleProvider) ->
                ParticleFactoryRegistry.getInstance().register(particleType, sprites -> particleProvider));
        COTWCommonClient.registerItemModelProperties(ItemProperties::register);
        COTWCommonClient.registerItemColors(ColorProviderRegistry.ITEM::register);
        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            ResourceLocation texture = AlternateArmorItem.getArmorTextureResource(stack, null);
            ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, contextModel, texture);
        }, ItemInit.HYLIAN_HELMET.get());
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
