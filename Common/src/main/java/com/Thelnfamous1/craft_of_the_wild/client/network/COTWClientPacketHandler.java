package com.Thelnfamous1.craft_of_the_wild.client.network;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

public class COTWClientPacketHandler {
    public void handleCircleParticlesPacket(ParticleOptions particle, double x, double y, double z, double xZRadius, int amount) {
        COTWUtil.spawnSmashAttackParticles(Minecraft.getInstance().level, particle, new Vec3(x, y, z), xZRadius, amount);
    }
}
