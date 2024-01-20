package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.jade.overlay.RayTracing;

@Pseudo
@Mixin(value = RayTracing.class, remap = false)
public class RayTracingMixin {

    @Inject(method = "canBeTarget", at = @At("HEAD"), cancellable = true, remap = false)
    private void handleCanBeTarget(Entity target, Entity viewEntity, CallbackInfoReturnable<Boolean> cir){
        if(Constants.FIX_JADE_MULTIPART_ENTITIES && target instanceof PartEntity<?>){
            cir.setReturnValue(false);
        }
    }
}
