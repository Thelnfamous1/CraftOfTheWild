package com.Thelnfamous1.craft_of_the_wild.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class AbstractSyncSaddlePacket {
    protected final int entityId;
    protected final ItemStack saddle;

    protected AbstractSyncSaddlePacket(int entityId, ItemStack saddle){
        this.entityId = entityId;
        this.saddle = saddle;
    }

    protected AbstractSyncSaddlePacket(FriendlyByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();
        this.saddle = byteBuf.readItem();
    }

    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(this.entityId);
        byteBuf.writeItem(this.saddle);
    }
}
