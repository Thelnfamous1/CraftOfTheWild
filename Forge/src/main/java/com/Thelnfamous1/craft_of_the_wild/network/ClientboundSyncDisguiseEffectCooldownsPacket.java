package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ClientboundSyncDisguiseEffectCooldownsPacket extends AbstractSyncDisguiseEffectCooldownsPacket {

    public ClientboundSyncDisguiseEffectCooldownsPacket(int entityId, Map<MobEffect, Integer> disguiseEffectCooldowns){
        super(entityId, disguiseEffectCooldowns);
    }

    public ClientboundSyncDisguiseEffectCooldownsPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> COTWCommonClient.getPacketHandler().handleSyncDisguiseEffectPacket(this.entityId, this.disguiseEffectCooldowns));
        ctx.get().setPacketHandled(true);
    }

}