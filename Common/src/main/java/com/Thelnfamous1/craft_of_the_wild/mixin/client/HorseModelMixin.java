package com.Thelnfamous1.craft_of_the_wild.mixin.client;

import com.Thelnfamous1.craft_of_the_wild.duck.SaddleEquipper;
import com.Thelnfamous1.craft_of_the_wild.item.TravelersSaddleItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HorseModel.class)
public abstract class HorseModelMixin<T extends AbstractHorse> extends AgeableListModel<T> {

    @WrapOperation(method = "setupAnim(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;isSaddled()Z"))
    private boolean wrap_isSaddled_setupAnim(AbstractHorse instance, Operation<Boolean> original){
        boolean saddled = original.call(instance);
        if(saddled && TravelersSaddleItem.is(((SaddleEquipper)instance).craft_of_the_wild$getSaddle())){
            return false;
        }
        return saddled;
    }
}
