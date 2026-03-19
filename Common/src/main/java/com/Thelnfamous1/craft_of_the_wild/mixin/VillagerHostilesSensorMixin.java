package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.animal.horse.Horse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {

    @WrapOperation(method = "isClose", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    private Object wrap_getAcceptableDistanceFromHostile_isClose(ImmutableMap<EntityType<?>, Float> instance, Object entityType, Operation<Object> original, LivingEntity villager, LivingEntity target){
        if(target instanceof Horse horse && horse.getArmor().is(ItemInit.MONSTER_HORSE_ARMOR.get())){
            return Float.valueOf(8.0F);
        } else{
            return original.call(instance, entityType);
        }
    }

    @WrapOperation(method = "isHostile", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;containsKey(Ljava/lang/Object;)Z", remap = false))
    private boolean wrap_getAcceptableDistanceFromHostile_isClose(ImmutableMap<EntityType<?>, Float> instance, Object entityType, Operation<Boolean> original, LivingEntity target){
        if(target instanceof Horse horse && horse.getArmor().is(ItemInit.MONSTER_HORSE_ARMOR.get())){
            return true;
        } else{
            return original.call(instance, entityType);
        }
    }
}
