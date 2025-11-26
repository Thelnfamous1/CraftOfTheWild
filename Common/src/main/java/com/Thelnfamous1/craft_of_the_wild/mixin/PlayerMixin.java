package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.init.CriterionInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    /*
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;turtleHelmetTick()V", shift = At.Shift.AFTER))
    private void post_turtleHelmetTick_tick(CallbackInfo ci){
        RadiantArmorItem.checkTick(this);
        BokolbinArmorItem.checkTick(this);
    }
     */

    @Inject(method = "killedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void inject_post_killedEntity(ServerLevel $$0, LivingEntity $$1, CallbackInfoReturnable<Boolean> cir){
        CriterionInit.PLAYER_KILLED_ENTITY_COUNT.trigger((ServerPlayer)(Object )this, $$1);
    }
}
