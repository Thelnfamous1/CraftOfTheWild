package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;

public class S2CCircleParticlesPacket extends AbstractCircleParticlesPacket implements FabricPacket {
    public static final PacketType<S2CCircleParticlesPacket> PACKET_TYPE = PacketType.create(COTWFabricNetwork.CIRCLE_PARTICLES, S2CCircleParticlesPacket::new);

    public <T extends ParticleOptions> S2CCircleParticlesPacket(T particle, double x, double y, double z, double xZRadius, int count){
        super(particle, x, y, z, xZRadius, count);
    }

    public S2CCircleParticlesPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public void receive(LocalPlayer player, PacketSender responseSender){
        COTWCommonClient.getPacketHandler().handleCircleParticlesPacket(this.particle, this.x, this.y, this.z, this.xZRadius, this.count);
    }

}