package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class S2CSyncSaddlePacket extends AbstractSyncSaddlePacket implements FabricPacket {
    public static final PacketType<S2CSyncSaddlePacket> PACKET_TYPE = PacketType.create(COTWFabricNetwork.SYNC_SADDLE, S2CSyncSaddlePacket::new);

    public S2CSyncSaddlePacket(int entityId, ItemStack saddle){
        super(entityId, saddle);
    }

    public S2CSyncSaddlePacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public void receive(LocalPlayer player, PacketSender responseSender){
        COTWCommonClient.getPacketHandler().handleSyncSaddlePacket(this.entityId, this.saddle);
    }

}