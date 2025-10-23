package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(StartAttacking.class)
public class StartAttackingMixin {
    @Unique
    private static LivingEntity currentTarget;

    @ModifyVariable(method = {"method_47123", "m_257271_", "lambda$create$1"}, at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private static LivingEntity captureTarget(LivingEntity target){
        currentTarget = target;
        return target;
    }

    @Inject(method = {"method_47123", "m_257271_", "lambda$create$1"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;set(Ljava/lang/Object;)V"), cancellable = true)
    private static void handleConditions(Predicate $$0x, Function $$1x, MemoryAccessor $$2x, MemoryAccessor $$3x, ServerLevel $$4, Mob attacker, long $$6, CallbackInfoReturnable<Boolean> cir){
        if(currentTarget != null && COTWCommon.isUnableToTarget(attacker, currentTarget)) {
            cir.setReturnValue(false);
        }
        if(currentTarget != null) currentTarget = null;
    }
}