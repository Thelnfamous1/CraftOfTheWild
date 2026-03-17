package com.Thelnfamous1.craft_of_the_wild.client;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.client.model.TravelersSaddleModel;
import com.Thelnfamous1.craft_of_the_wild.client.network.COTWClientPacketHandler;
import com.Thelnfamous1.craft_of_the_wild.client.particle.CustomTerrainParticle;
import com.Thelnfamous1.craft_of_the_wild.client.renderer.*;
import com.Thelnfamous1.craft_of_the_wild.compat.dynamiclights.COTWDynamicLightHandlers;
import com.Thelnfamous1.craft_of_the_wild.duck.CustomMusicListener;
import com.Thelnfamous1.craft_of_the_wild.entity.CustomMusicPlayer;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.init.ParticleInit;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class COTWCommonClient {

    public static final ResourceLocation GLOW_ITEM_PROPERTY = COTWCommon.getResourceLocation("glow");
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation TRAVELERS_SADDLE_LAYER = new ModelLayerLocation(COTWCommon.getResourceLocation("travelers_saddle"), "main");
    public static final ResourceLocation TRAVELERS_SADDLE_TEXTURE = COTWCommon.getResourceLocation("textures/entity/horse/travelers_saddle.png");
    private static COTWClientPacketHandler packetHandler;

    public static void init(){
        packetHandler = new COTWClientPacketHandler();
    }

    public static void registerRenderers(BiConsumer<EntityType, EntityRendererProvider> register){
        registerRendererTyped(EntityInit.STONE_TALUS.get(), renderManager -> new StoneTalusRenderer<>(renderManager, EntityInit.STONE_TALUS.getId()), register);
        registerRendererTyped(EntityInit.FROST_TALUS.get(), renderManager -> new GlowingStoneTalusRenderer<>(renderManager, EntityInit.FROST_TALUS.getId()), register);
        registerRendererTyped(EntityInit.IGNEO_TALUS.get(), renderManager -> new GlowingStoneTalusRenderer<>(renderManager, EntityInit.IGNEO_TALUS.getId()), register);
        registerRendererTyped(EntityInit.STONE_TALUS_RARE.get(), renderManager -> new GlowingStoneTalusRenderer<>(renderManager, EntityInit.STONE_TALUS_RARE.getId()), register);
        registerRendererTyped(EntityInit.STONE_TALUS_LUMINOUS.get(), renderManager -> new GlowingStoneTalusRenderer<>(renderManager, EntityInit.STONE_TALUS_LUMINOUS.getId()), register);
        registerRendererTyped(EntityInit.STONE_TALUS_ARM.get(), renderManager -> new StoneTalusArmRenderer<>(renderManager, EntityInit.STONE_TALUS_ARM.getId()), register);
        registerRendererTyped(EntityInit.FROST_TALUS_ARM.get(), renderManager -> new StoneTalusArmRenderer<>(renderManager, EntityInit.FROST_TALUS_ARM.getId()), register);
        registerRendererTyped(EntityInit.IGNEO_TALUS_ARM.get(), renderManager -> new GlowingStoneTalusArmRenderer<>(renderManager, EntityInit.IGNEO_TALUS_ARM.getId()), register);
        registerRendererTyped(EntityInit.BEEDLE.get(), BeedleRenderer::new, register);
        registerRendererTyped(EntityInit.KASS.get(), KassRenderer::new, register);
        registerRendererTyped(EntityInit.STONE_PEBBLIT.get(), renderManager -> new StonePebblitRenderer<>(renderManager, EntityInit.STONE_PEBBLIT.getId()), register);
        registerRendererTyped(EntityInit.FROST_PEBBLIT.get(), renderManager -> new StonePebblitRenderer<>(renderManager, EntityInit.FROST_PEBBLIT.getId()), register);
        registerRendererTyped(EntityInit.IGNEO_PEBBLIT.get(), renderManager -> new GlowingStonePebblitRenderer<>(renderManager, EntityInit.IGNEO_PEBBLIT.getId()), register);
        registerRendererTyped(EntityInit.KILTON.get(), KiltonRenderer::new, register);
    }

    private static <T extends Entity> void registerRendererTyped(EntityType<T> type, EntityRendererProvider<T> provider, BiConsumer<EntityType, EntityRendererProvider> register) {
        register.accept(type, provider);
    }

    public static void registerColorHandlers(BiConsumer<ItemColor, ItemLike> register) {
        COTWSpawnEggItem.getEggs().forEach(egg -> register.accept((stack, layer) -> egg.getColor(layer), egg));
    }

    public static <T extends LivingEntity & CustomMusicPlayer> void playCustomMusicFor(T boss) {
        if (Minecraft.getInstance().player != null) {
            ((CustomMusicListener)Minecraft.getInstance().player).craft_of_the_wild$getCustomMusicSoundHandler().playCustomMusicFor(boss);
        }
    }

    public static <T extends LivingEntity & CustomMusicPlayer> void stopCustomMusicFor(T boss) {
        if (Minecraft.getInstance().player != null) {
            ((CustomMusicListener)Minecraft.getInstance().player).craft_of_the_wild$getCustomMusicSoundHandler().stopCustomMusicFor(boss);
        }
    }

    public static COTWClientPacketHandler getPacketHandler() {
        return packetHandler;
    }

    public static void setup(){
        if(Services.PLATFORM.isModLoaded(Constants.SODIUM_DYNAMIC_LIGHTS_MODID)){
            DynamicLightHandlers.registerDynamicLightHandler(EntityInit.BEEDLE.get(), COTWDynamicLightHandlers.BEEDLE);
        }
    }

    public static void registerParticles(BiConsumer<ParticleType, ParticleProvider> register) {
        register.accept(ParticleInit.DUST_PILLAR.get(), new CustomTerrainParticle.DustPillarProvider());
    }

    public static void registerItemModelProperties(ItemPropertyRegistration callback){
        callback.apply(ItemInit.LUMINOUS_STONE.get(), GLOW_ITEM_PROPERTY, (itemStack, clientLevel, livingEntity, seed) -> {
            if (clientLevel == null) return 0;
            Entity context = livingEntity != null ? livingEntity : itemStack.getEntityRepresentation();
            if (context == null) return 0;

            return COTWUtil.getLocalDarknessFactor(0.0F, clientLevel, context.blockPosition());
        });
    }

    public static void registerItemColors(ItemColorRegistration colorRegistration) {
        colorRegistration.apply((itemStack, i) -> {
                    if (i > 0) return -1; // overlay layer, no tint
                    DyeableLeatherItem item = (DyeableLeatherItem) itemStack.getItem();
                    // Only apply color if the stack has a custom color
                    return item.hasCustomColor(itemStack) ? item.getColor(itemStack) : -1;
                },
                ItemInit.RADIANT_HELMET.get(), ItemInit.RADIANT_CHESTPLATE.get(), ItemInit.RADIANT_LEGGINGS.get(), ItemInit.RADIANT_BOOTS.get());
    }

    public static void registerModelLayers(ModelLayerRegistration callback){
        callback.register(TRAVELERS_SADDLE_LAYER, TravelersSaddleModel::createBodyLayer);
    }

    public static void addEntityRenderLayers(EntityRendererGetter entityRendererGetter, EntityRendererLayerAdder adder, EntityRendererProvider.Context context) {
        if(entityRendererGetter.get(EntityType.HORSE) instanceof HorseRenderer horseRenderer){
            adder.add(horseRenderer, new TravelersSaddleLayer<>(horseRenderer,
                    new TravelersSaddleModel<>(
                            context.bakeLayer(ModelLayers.HORSE),
                            context.bakeLayer(TRAVELERS_SADDLE_LAYER)),
                    TRAVELERS_SADDLE_TEXTURE));
        }
    }

    @FunctionalInterface
    public interface ItemPropertyRegistration{
        void apply(Item pItem, ResourceLocation pName, ClampedItemPropertyFunction pProperty);
    }

    @FunctionalInterface
    public interface ItemColorRegistration{
        void apply(ItemColor itemColor, ItemLike... itemLikes);
    }

    @FunctionalInterface
    public interface EntityRendererGetter {
        LivingEntityRenderer<?, ? extends EntityModel<?>> get(EntityType<?> type);
    }

    @FunctionalInterface
    public interface EntityRendererLayerAdder {
       void add(LivingEntityRenderer renderer, RenderLayer layer);
    }

    @FunctionalInterface
    public interface ModelLayerRegistration {
        void register(ModelLayerLocation location, Supplier<LayerDefinition> layerDefinition);
    }


}
