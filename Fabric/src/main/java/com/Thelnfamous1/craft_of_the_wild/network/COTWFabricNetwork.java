package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class COTWFabricNetwork {

    public static final ResourceLocation CIRCLE_PARTICLES = COTWCommon.getResourceLocation("circle_particles");
    public static final ResourceLocation SYNC_DISGUISE_EFFECT_COOLDOWNS = COTWCommon.getResourceLocation("sync_disguise_effect_cooldowns");

    public static void registerClientPackets(){
        ClientPlayNetworking.registerGlobalReceiver(S2CCircleParticlesPacket.PACKET_TYPE, S2CCircleParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(S2CSyncDisguiseEffectCooldownsPacket.PACKET_TYPE, S2CSyncDisguiseEffectCooldownsPacket::receive);
    }

}