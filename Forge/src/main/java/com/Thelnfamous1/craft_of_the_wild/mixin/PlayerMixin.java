package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusAttackType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow public abstract void disableShield(boolean pBecauseOfAxe);

    @Inject(method = "blockUsingShield", at = @At("HEAD"), cancellable = true)
    private void handleBlockUsingShield(LivingEntity pEntity, CallbackInfo ci){
        if(pEntity instanceof StoneTalus talus && talus.getCurrentAttackType() == StoneTalusAttackType.PUNCH){
            this.disableShield(true);
            ci.cancel();
        }
    }
}
