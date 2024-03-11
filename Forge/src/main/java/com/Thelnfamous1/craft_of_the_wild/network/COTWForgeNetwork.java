package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class COTWForgeNetwork {
    private static final ResourceLocation CHANNEL_NAME = COTWCommon.getResourceLocation("network");
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel SYNC_CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME, () -> "1.0",
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int INDEX;

    public static void init() {
        SYNC_CHANNEL.registerMessage(INDEX++, ClientboundCircleParticlesPacket.class, ClientboundCircleParticlesPacket::write, ClientboundCircleParticlesPacket::new, ClientboundCircleParticlesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
