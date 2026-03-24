package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.duck.ChestableHorse;
import com.Thelnfamous1.craft_of_the_wild.item.SaddleArmor;
import com.Thelnfamous1.craft_of_the_wild.item.TravelersSaddleItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.TickTask;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Horse.class)
public abstract class HorseMixin extends AbstractHorseMixin implements ChestableHorse {
    @Unique
    private boolean craft_of_the_wild$hasChest;

    protected HorseMixin(EntityType<? extends AbstractHorse> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean craft_of_the_wild$hasChest() {
        return this.craft_of_the_wild$hasChest;
    }

    @Override
    public void craft_of_the_wild$setChest(boolean pChested) {
        this.craft_of_the_wild$hasChest = pChested;
    }

    @Override
    public void craft_of_the_wild$setClientSaddle(ItemStack saddle) {
        super.craft_of_the_wild$setClientSaddle(saddle);
        this.craft_of_the_wild$setChest(craft_of_the_wild$isChestSaddle(saddle));
    }

    @Unique
    private static boolean craft_of_the_wild$isChestSaddle(ItemStack saddle) {
        return TravelersSaddleItem.is(saddle);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void post_addAdditionalSaveData(CompoundTag $$0, CallbackInfo ci){
        this.craft_of_the_wild$writeChestableData($$0, this.inventory);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void post_readAdditionalSaveData(CompoundTag $$0, CallbackInfo ci){
        this.craft_of_the_wild$readChestableData($$0, this);
    }

    @Override
    public void craft_of_the_wild$createChestableInventory() {
        this.createInventory();
    }

    @Override
    public SimpleContainer craft_of_the_wild$getChestableInventory() {
        return this.inventory;
    }

    @Override
    protected int craft_of_the_wild$modifyInventorySize(int inventorySize) {
        return this.craft_of_the_wild$hasChest() ? this.craft_of_the_wild$getChestedInventorySize() : inventorySize;
    }

    @Override
    public boolean craft_of_the_wild$isValidSaddle(ItemStack saddle, boolean isVanillaSaddle) {
        return isVanillaSaddle || TravelersSaddleItem.is(saddle);
    }

    @Inject(method = "updateContainerEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/Horse;setArmorEquipment(Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    private void inject_setArmorEquipment_updateArmorEquipment(CallbackInfo ci){
        this.craft_of_the_wild$setSaddleArmor(this.inventory.getItem(0));
    }

    @Unique
    private void craft_of_the_wild$setSaddleArmor(ItemStack saddleStack) {
        if (!this.level().isClientSide) {
            this.getAttribute(Attributes.ARMOR).removeModifier(SaddleArmor.SADDLE_ARMOR_MODIFIER_UUID);
            if (saddleStack.getItem() instanceof SaddleArmor saddleArmor) {
                int protection = saddleArmor.getProtection();
                if (protection != 0) {
                    this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(SaddleArmor.SADDLE_ARMOR_MODIFIER_UUID, "Saddle armor bonus", protection, AttributeModifier.Operation.ADDITION));
                }
            }
        }

    }

    @Inject(method = "updateContainerEquipment", at = @At(value = "TAIL"))
    private void post_containerChanged(CallbackInfo ci){
        if(!this.level().isClientSide){
            boolean hadChest = this.craft_of_the_wild$hasChest();
            boolean hasChestNow = craft_of_the_wild$isChestSaddle(this.craft_of_the_wild$getSaddle());

            if (hadChest != hasChestNow) {
                this.craft_of_the_wild$setChest(hasChestNow);
                if (!hasChestNow) {
                    for (int i = ChestableHorse.DEFAULT_UNCHESTED_HORSE_INVENTORY_SIZE; i < this.inventory.getContainerSize(); ++i) {
                        ItemStack stack = this.inventory.getItem(i);
                        if (!stack.isEmpty()) {
                            this.spawnAtLocation(stack.copy());
                            this.inventory.setItem(i, ItemStack.EMPTY);
                        }
                    }
                }
                // Fixes CME when the HorseInventoryMenu is open and picking up/placing the chest saddle
                // This will also cause the horse inventory GUI to close as the horse's inventory is now no longer the same object
                this.level().getServer().tell(new TickTask(this.level().getServer().getTickCount(), this::createInventory));
            }
        }
    }
}
