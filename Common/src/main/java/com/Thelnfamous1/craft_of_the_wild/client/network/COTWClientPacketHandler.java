package com.Thelnfamous1.craft_of_the_wild.client.network;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;

public class COTWClientPacketHandler {
    public void handleCircleParticlesPacket(ParticleOptions particle, double x, double y, double z, double xZRadius, int amount) {
        COTWUtil.spawnParticlesInCircle(Minecraft.getInstance().level, particle, x, y, z, xZRadius, amount);
    }
}
