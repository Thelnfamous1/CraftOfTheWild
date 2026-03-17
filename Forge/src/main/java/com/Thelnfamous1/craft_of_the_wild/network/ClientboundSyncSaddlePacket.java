package com.Thelnfamous1.craft_of_the_wild.network;

import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncSaddlePacket extends AbstractSyncSaddlePacket {

    public ClientboundSyncSaddlePacket(int entityId, ItemStack saddle){
        super(entityId, saddle);
    }

    public ClientboundSyncSaddlePacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> COTWCommonClient.getPacketHandler().handleSyncSaddlePacket(this.entityId, this.saddle));
        ctx.get().setPacketHandled(true);
    }

}