package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.duck.SaddleEquipper;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class EntityTrackerMixin {

    @Shadow @Final private Entity entity;

    @Inject(method = "addPairing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;startSeenByPlayer(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.AFTER))
    private void post_startSeenByPlayer_addPairing(ServerPlayer player, CallbackInfo ci){
        if(this.entity instanceof SaddleEquipper saddleEquipper){
            Services.PLATFORM.sendSyncSaddlePacket(player, this.entity, saddleEquipper.craft_of_the_wild$getSaddle());
        }
    }
}
