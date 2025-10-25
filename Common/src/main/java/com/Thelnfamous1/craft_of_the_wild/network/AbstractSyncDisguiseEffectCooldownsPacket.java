package com.Thelnfamous1.craft_of_the_wild.network;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.Map;

public class AbstractSyncDisguiseEffectCooldownsPacket {
    protected final int entityId;
    protected final Map<MobEffect, Integer> disguiseEffectCooldowns;

    protected AbstractSyncDisguiseEffectCooldownsPacket(int entityId, Map<MobEffect, Integer> disguiseEffectCooldowns){
        this.entityId = entityId;
        this.disguiseEffectCooldowns = new HashMap<>(disguiseEffectCooldowns);
    }

    protected AbstractSyncDisguiseEffectCooldownsPacket(FriendlyByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();
        this.disguiseEffectCooldowns = byteBuf.readMap(b -> b.readById(BuiltInRegistries.MOB_EFFECT), FriendlyByteBuf::readInt);
    }

    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(this.entityId);
        byteBuf.writeMap(this.disguiseEffectCooldowns, (b, k) -> b.writeId(BuiltInRegistries.MOB_EFFECT, k), FriendlyByteBuf::writeInt);
    }
}
