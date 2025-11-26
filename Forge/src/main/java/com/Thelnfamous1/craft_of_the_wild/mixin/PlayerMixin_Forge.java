package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.entity.AnimatedAttacker;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusAttackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin_Forge extends LivingEntity{

    protected PlayerMixin_Forge(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract void disableShield(boolean pBecauseOfAxe);

    @Inject(method = "blockUsingShield", at = @At("HEAD"), cancellable = true)
    private void handleBlockUsingShield(LivingEntity pEntity, CallbackInfo ci){
        if(pEntity instanceof StoneTalus talus && AnimatedAttacker.hasCurrentAttackType(talus, StoneTalusAttackType.PUNCH)){
            this.disableShield(true);
            ci.cancel();
        }
    }


    @ModifyVariable(method = "getDigSpeed", remap = false, at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private int modify_efficiencyLevel_getDestroySpeed(int original){
        if(COTWCommon.isMedalOfHonorTalusEquipped(this)){
            original += 2;
        }
        return original;
    }

}
