package com.Thelnfamous1.craft_of_the_wild.duck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ChestableHorse {
    String CHESTED_HORSE_NBT_TAG_KEY = "ChestedHorse";
    String INVENTORY_SLOT_TAG_KEY = "Slot";
    String INVENTORY_ITEMS_TAG_KEY = "Items";
    int DEFAULT_UNCHESTED_HORSE_INVENTORY_SIZE = 2;
    int DEFAULT_CHESTED_HORSE_INVENTORY_COLUMNS = 5;

    boolean craft_of_the_wild$hasChest();

    void craft_of_the_wild$setChest(boolean pChested);

    default int craft_of_the_wild$getInventoryColumns() {
        return DEFAULT_CHESTED_HORSE_INVENTORY_COLUMNS;
    }

    default int craft_of_the_wild$getChestedInventorySize(){
        return DEFAULT_UNCHESTED_HORSE_INVENTORY_SIZE + (3 * this.craft_of_the_wild$getInventoryColumns());
    }

    default void craft_of_the_wild$writeChestableData(CompoundTag pCompound, SimpleContainer inventory){
        pCompound.putBoolean(CHESTED_HORSE_NBT_TAG_KEY, this.craft_of_the_wild$hasChest());
        if (this.craft_of_the_wild$hasChest()) {
            ListTag listtag = new ListTag();

            for(int i = DEFAULT_UNCHESTED_HORSE_INVENTORY_SIZE; i < inventory.getContainerSize(); ++i) {
                ItemStack itemstack = inventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag compoundtag = new CompoundTag();
                    compoundtag.putByte(INVENTORY_SLOT_TAG_KEY, (byte)i);
                    itemstack.save(compoundtag);
                    listtag.add(compoundtag);
                }
            }

            pCompound.put(INVENTORY_ITEMS_TAG_KEY, listtag);
        }
    }

    default void craft_of_the_wild$readChestableData(CompoundTag pCompound, @Nullable Entity chestedEntity){
        this.craft_of_the_wild$setChest(pCompound.getBoolean(CHESTED_HORSE_NBT_TAG_KEY));
        //this.createInventory();
        this.craft_of_the_wild$createChestableInventory(); // re-creates inventory, replacing the field entirely
        if (this.craft_of_the_wild$hasChest()) {
            ListTag inventoryItemsTag = pCompound.getList(INVENTORY_ITEMS_TAG_KEY, ListTag.TAG_COMPOUND);

            for(int i = 0; i < inventoryItemsTag.size(); ++i) {
                CompoundTag inventoryItemTag = inventoryItemsTag.getCompound(i);
                ItemStack inventoryItem = ItemStack.of(inventoryItemTag);
                int slot = inventoryItemTag.getByte(INVENTORY_SLOT_TAG_KEY) & 255;
                SimpleContainer inventory = this.craft_of_the_wild$getChestableInventory();
                if (slot >= DEFAULT_UNCHESTED_HORSE_INVENTORY_SIZE && slot < inventory.getContainerSize()) {
                    inventory.setItem(slot, inventoryItem);
                } else{
                    if(chestedEntity != null && chestedEntity.level().isClientSide && !inventoryItem.isEmpty()){
                        chestedEntity.spawnAtLocation(inventoryItem);
                    }
                }
            }
        }
    }

    SimpleContainer craft_of_the_wild$getChestableInventory();

    void craft_of_the_wild$createChestableInventory();
}
