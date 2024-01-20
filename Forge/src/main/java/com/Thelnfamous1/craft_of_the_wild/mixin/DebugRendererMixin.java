package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(value = DebugRenderer.class)
public class DebugRendererMixin {

    @ModifyVariable(method = "getTargetedEntity", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private static Predicate<Entity> modifyHitResultPredicate(Predicate<Entity> value){
        if(Constants.FIX_DEBUG_UTILS_MULTIPART_ENTITIES){
            return (entity) -> !entity.isSpectator() && (entity.isPickable() && !(entity instanceof PartEntity<?>) || entity.isMultipartEntity());
        }
        return value;
    }
}
