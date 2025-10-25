package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;

import java.util.Map;

public class S2CSyncDisguiseEffectCooldownsPacket extends AbstractSyncDisguiseEffectCooldownsPacket implements FabricPacket {
    public static final PacketType<S2CSyncDisguiseEffectCooldownsPacket> PACKET_TYPE = PacketType.create(COTWFabricNetwork.SYNC_DISGUISE_EFFECT_COOLDOWNS, S2CSyncDisguiseEffectCooldownsPacket::new);

    public S2CSyncDisguiseEffectCooldownsPacket(int entityId, Map<MobEffect, Integer> disguiseEffectCooldowns){
        super(entityId, disguiseEffectCooldowns);
    }

    public S2CSyncDisguiseEffectCooldownsPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public void receive(LocalPlayer player, PacketSender responseSender){
        COTWCommonClient.getPacketHandler().handleSyncDisguiseEffectPacket(this.entityId, this.disguiseEffectCooldowns);
    }

}