package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.item.TravelersSaddleItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/world/inventory/HorseInventoryMenu$1")
public class HorseInventoryMenuSaddleSlotMixin {

    @WrapOperation(method = "mayPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
    private boolean wrap_isSaddle_mayPlace(ItemStack instance, Item pItem, Operation<Boolean> original){
        if(TravelersSaddleItem.is(instance)){
            return true;
        }
        return original.call(instance, pItem);
    }
}
