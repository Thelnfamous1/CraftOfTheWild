package com.Thelnfamous1.craft_of_the_wild.client;

import com.Thelnfamous1.craft_of_the_wild.client.network.COTWClientPacketHandler;
import com.Thelnfamous1.craft_of_the_wild.client.particle.CustomTerrainParticle;
import com.Thelnfamous1.craft_of_the_wild.client.renderer.BeedleRenderer;
import com.Thelnfamous1.craft_of_the_wild.client.renderer.KassRenderer;
import com.Thelnfamous1.craft_of_the_wild.client.renderer.StoneTalusArmRenderer;
import com.Thelnfamous1.craft_of_the_wild.client.renderer.StoneTalusRenderer;
import com.Thelnfamous1.craft_of_the_wild.compat.dynamiclights.COTWDynamicLightHandlers;
import com.Thelnfamous1.craft_of_the_wild.duck.CustomMusicListener;
import com.Thelnfamous1.craft_of_the_wild.entity.CustomMusicPlayer;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.ParticleInit;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ItemLike;

import java.util.function.BiConsumer;

public class COTWCommonClient {

    private static COTWClientPacketHandler packetHandler;

    public static void init(){
        packetHandler = new COTWClientPacketHandler();
    }

    public static void registerRenderers(BiConsumer<EntityType, EntityRendererProvider> register){
        registerRendererTyped(EntityInit.STONE_TALUS.get(), StoneTalusRenderer::new, register);
        registerRendererTyped(EntityInit.STONE_TALUS_ARM.get(), StoneTalusArmRenderer::new, register);
        registerRendererTyped(EntityInit.BEEDLE.get(), BeedleRenderer::new, register);
        registerRendererTyped(EntityInit.KASS.get(), KassRenderer::new, register);
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
        if(Services.PLATFORM.isModLoaded("sodiumdynamiclights")){
            DynamicLightHandlers.registerDynamicLightHandler(EntityInit.BEEDLE.get(), COTWDynamicLightHandlers.BEEDLE);
        }
    }

    public static void registerParticles(BiConsumer<ParticleType, ParticleProvider> register) {
        register.accept(ParticleInit.DUST_PILLAR.get(), new CustomTerrainParticle.DustPillarProvider());
    }
}
