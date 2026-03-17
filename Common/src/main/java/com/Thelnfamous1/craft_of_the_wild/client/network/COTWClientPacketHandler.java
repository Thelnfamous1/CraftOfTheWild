package com.Thelnfamous1.craft_of_the_wild.client.network;

import com.Thelnfamous1.craft_of_the_wild.duck.DisguiseEffectUser;
import com.Thelnfamous1.craft_of_the_wild.duck.SaddleEquipper;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class COTWClientPacketHandler {
    public void handleCircleParticlesPacket(ParticleOptions particle, double x, double y, double z, double xZRadius, int amount) {
        COTWUtil.spawnSmashAttackParticles(Minecraft.getInstance().level, particle, new Vec3(x, y, z), xZRadius, amount);
    }

    public void handleSyncDisguiseEffectPacket(int entityId, Map<MobEffect, Integer> disguiseEffectCooldowns) {
        Entity entity = Minecraft.getInstance().level.getEntity(entityId);
        if(entity instanceof DisguiseEffectUser disguiseEffectUser){
            disguiseEffectUser.craft_of_the_wild$syncDisguiseEffectCooldowns(disguiseEffectCooldowns);
        }
    }

    public void handleSyncSaddlePacket(int entityId, ItemStack saddle) {
        Entity entity = Minecraft.getInstance().level.getEntity(entityId);
        if(entity instanceof SaddleEquipper saddleEquipper){
            saddleEquipper.craft_of_the_wild$setClientSaddle(saddle);
        }
    }
}
