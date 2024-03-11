package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class COTWFabricNetwork {

    public static final ResourceLocation CIRCLE_PARTICLES = COTWCommon.getResourceLocation("circle_particles");

    public static void registerClientPackets(){
        ClientPlayNetworking.registerReceiver(S2CCircleParticlesPacket.PACKET_TYPE, S2CCircleParticlesPacket::receive);
    }

}