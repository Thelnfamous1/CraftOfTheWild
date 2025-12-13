package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.duck.FreezeAttackVictim;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements FreezeAttackVictim {

    @Shadow public abstract int getTicksFrozen();

    @Unique
    private int craft_of_the_wild$$pauseFrozenTicks;

    @Override
    public int craft_of_the_wild$getPauseFrozenTicks() {
        return this.craft_of_the_wild$$pauseFrozenTicks;
    }

    @Override
    public void craft_of_the_wild$setPauseFrozenTicks(int pauseFrozenTicks) {
        this.craft_of_the_wild$$pauseFrozenTicks = pauseFrozenTicks;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void post_tick(CallbackInfo ci){
        boolean wasPaused = this.craft_of_the_wild$getPauseFrozenTicks() > 0;
        this.craft_of_the_wild$$pauseFrozenTicks = Math.max(0, this.craft_of_the_wild$$pauseFrozenTicks - 1);
        if(wasPaused && this.craft_of_the_wild$getPauseFrozenTicks() <= 0){
            COTWCommon.debug(Constants.DEBUG_PAUSE_FROZEN_TICKS, "Frozen ticks have been unpaused in EntityMixin for {} with {} ticks frozen", this, this.getTicksFrozen());
        }
    }
}
