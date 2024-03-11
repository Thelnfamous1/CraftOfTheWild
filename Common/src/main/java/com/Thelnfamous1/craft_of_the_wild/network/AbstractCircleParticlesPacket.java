package com.Thelnfamous1.craft_of_the_wild.network;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class AbstractCircleParticlesPacket {
    protected final ParticleOptions particle;
    protected final double x;
    protected final double y;
    protected final double z;
    protected final double xZRadius;
    protected final int count;

    protected <T extends ParticleOptions> AbstractCircleParticlesPacket(T particle, double x, double y, double z, double xZRadius, int count){
        this.particle = particle;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xZRadius = xZRadius;
        this.count = count;
    }

    protected AbstractCircleParticlesPacket(FriendlyByteBuf byteBuf) {
        ParticleType<?> particleType = byteBuf.readById(BuiltInRegistries.PARTICLE_TYPE);
        this.particle = this.readParticle(byteBuf, particleType);
        this.x = byteBuf.readDouble();
        this.y = byteBuf.readDouble();
        this.z = byteBuf.readDouble();
        this.xZRadius = byteBuf.readDouble();
        this.count = byteBuf.readInt();
    }

    protected <T extends ParticleOptions> T readParticle(FriendlyByteBuf pBuffer, ParticleType<T> pParticleType) {
        return pParticleType.getDeserializer().fromNetwork(pParticleType, pBuffer);
    }

    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeId(BuiltInRegistries.PARTICLE_TYPE, this.particle.getType());
        this.particle.writeToNetwork(byteBuf);
        byteBuf.writeDouble(this.x);
        byteBuf.writeDouble(this.y);
        byteBuf.writeDouble(this.z);
        byteBuf.writeDouble(this.xZRadius);
        byteBuf.writeInt(this.count);
    }
}
