package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

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
}
