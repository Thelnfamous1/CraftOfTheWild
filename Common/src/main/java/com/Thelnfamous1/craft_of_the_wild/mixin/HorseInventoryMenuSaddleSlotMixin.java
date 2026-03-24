package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.duck.SaddleEquipper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/world/inventory/HorseInventoryMenu$1")
public class HorseInventoryMenuSaddleSlotMixin {

    @Shadow @Final
    AbstractHorse val$pHorse;

    @WrapOperation(method = "mayPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
    private boolean wrap_isSaddle_mayPlace(ItemStack instance, Item pItem, Operation<Boolean> original){
        return ((SaddleEquipper)this.val$pHorse).craft_of_the_wild$isValidSaddle(instance, original.call(instance, pItem));
    }
}
