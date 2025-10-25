package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.duck.DisguiseEffectUser;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements DisguiseEffectUser {

    @Shadow public abstract boolean removeEffect(MobEffect pEffect);

    @Shadow public abstract void handleEntityEvent(byte pId);

    @Unique
    private Map<MobEffect, Integer> craft_of_the_wild$$disguiseEffectCooldowns = new HashMap<>();

    public LivingEntityMixin(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    public int craft_of_the_wild$getDisguiseEffectCooldownTicks(MobEffect disguiseEffect) {
        return this.craft_of_the_wild$$disguiseEffectCooldowns.getOrDefault(disguiseEffect, 0);
    }

    @Override
    public void craft_of_the_wild$setDisguiseEffectCooldownTicks(MobEffect disguiseEffect, int disguiseCooldownTicks) {
        this.craft_of_the_wild$$disguiseEffectCooldowns.put(disguiseEffect, disguiseCooldownTicks);
        if(!this.level().isClientSide){
            Services.PLATFORM.sendSyncDisguiseEffectPacket(this, this.craft_of_the_wild$$disguiseEffectCooldowns);
        }
    }

    @Override
    public void craft_of_the_wild$syncDisguiseEffectCooldowns(Map<MobEffect, Integer> disguiseEffectCooldowns) {
        this.craft_of_the_wild$$disguiseEffectCooldowns.clear();
        for(Map.Entry<MobEffect, Integer> disguiseEffectCooldown : disguiseEffectCooldowns.entrySet()){
            if(disguiseEffectCooldown.getValue() > 0){
                this.craft_of_the_wild$removeDisguiseEffect(disguiseEffectCooldown.getKey());
            }
        }
        this.craft_of_the_wild$$disguiseEffectCooldowns.putAll(disguiseEffectCooldowns);
    }

    @Override
    public void craft_of_the_wild$removeDisguiseEffect(MobEffect disguiseEffect) {
        this.removeEffect(disguiseEffect);
        this.level().broadcastEntityEvent(this, EntityEvent.POOF);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void post_tick(CallbackInfo ci){
        Iterator<Map.Entry<MobEffect, Integer>> iterator = this.craft_of_the_wild$$disguiseEffectCooldowns.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<MobEffect, Integer> disguiseEffectCooldown = iterator.next();
            int cooldown = disguiseEffectCooldown.getValue();
            if (cooldown <= 1) {
                iterator.remove();
            } else {
                disguiseEffectCooldown.setValue(cooldown - 1);
            }
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", shift = At.Shift.AFTER))
    private void post_actuallyHurt_hurt(DamageSource $$0, float $$1, CallbackInfoReturnable<Boolean> cir){
        if($$0.getEntity() instanceof DisguiseEffectUser disguisedAttacker){
            MobEffect disguiseEffect = disguisedAttacker.craft_of_the_wild$getAffectingDisguiseEffect(this);
            if(disguiseEffect != null && ((LivingEntity)disguisedAttacker).hasEffect(disguiseEffect)){
                disguisedAttacker.craft_of_the_wild$removeDisguiseEffect(disguiseEffect);
                disguisedAttacker.craft_of_the_wild$cooldownDisguiseEffect(disguiseEffect);
            }
        }
    }
}
