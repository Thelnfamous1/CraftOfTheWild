package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundCircleParticlesPacket extends AbstractCircleParticlesPacket{

    public <T extends ParticleOptions> ClientboundCircleParticlesPacket(T particle, double x, double y, double z, double xZRadius, int count){
        super(particle, x, y, z, xZRadius, count);
    }

    public ClientboundCircleParticlesPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> COTWCommonClient.getPacketHandler().handleCircleParticlesPacket(this.particle, this.x, this.y, this.z, this.xZRadius, this.count));
        ctx.get().setPacketHandled(true);
    }

}