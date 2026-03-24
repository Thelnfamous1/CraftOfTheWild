package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.duck.SaddleEquipper;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements SaddleEquipper {
    @Unique
    private ItemStack craft_of_the_wild$clientSaddle = ItemStack.EMPTY;

    @Shadow protected SimpleContainer inventory;

    @Shadow protected abstract void createInventory();

    protected AbstractHorseMixin(EntityType<? extends Animal> $$0, Level $$1) {
        super($$0, $$1);
    }

    @WrapOperation(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
    private boolean wrap_isSaddle_readAdditionalSaveData(ItemStack instance, Item pItem, Operation<Boolean> original){
        return this.craft_of_the_wild$isValidSaddle(instance, original.call(instance, pItem));
    }

    @Override
    public void craft_of_the_wild$equipSaddle(SoundSource source, ItemStack saddle) {
        this.inventory.setItem(0, saddle);
        //this.craft_of_the_wild$onSaddleEquipped(saddle);
    }

    @Override
    public ItemStack craft_of_the_wild$getSaddle() {
        if(!this.level().isClientSide){
            return this.inventory.getItem(0);
        }
        return this.craft_of_the_wild$clientSaddle;
    }

    @Override
    public void craft_of_the_wild$setClientSaddle(ItemStack saddle) {
        this.craft_of_the_wild$clientSaddle = saddle;
    }

    @Inject(method = "updateContainerEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;setFlag(IZ)V", shift = At.Shift.AFTER, ordinal = 0))
    protected void post_updateContainerEquipment(CallbackInfo ci){
        if(!this.level().isClientSide){
            Services.PLATFORM.sendSyncSaddlePacket(null, this, this.inventory.getItem(0));
        }
    }

    /*
    @WrapOperation(method = {"method_32337", "m_149517_", "lambda$getSlot$7"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
    private static boolean wrap_isSaddle_getSlot(ItemStack instance, Item pItem, Operation<Boolean> original){
        if(TravelersSaddleItem.is(instance)){
            return true;
        }
        return original.call(instance, pItem);
    }
     */

    @Inject(method = "getInventorySize", at = @At("RETURN"), cancellable = true)
    private void post_getInventorySize(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(this.craft_of_the_wild$modifyInventorySize(cir.getReturnValue()));
    }

    @Unique
    protected int craft_of_the_wild$modifyInventorySize(int inventorySize){
        return inventorySize;
    }
}
