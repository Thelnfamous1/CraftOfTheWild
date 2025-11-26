package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class PlayerMixin_Fabric extends LivingEntity {

    protected PlayerMixin_Fabric(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "getDestroySpeed", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private int modify_efficiencyLevel_getDestroySpeed(int original){
        if(COTWCommon.isMedalOfHonorTalusEquipped(this)){
            original += 2;
        }
        return original;
    }
}
