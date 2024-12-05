package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Thomas Glasser
 */
@Mixin(StructureTemplate.class)
public class StructureTemplateMixin
{
    // Injects into StructureTemplate#placeEntities, inside the lambda of createEntityIgnoreException
    @Inject(method = "lambda$addEntitiesToWorld$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;moveTo(DDDFF)V", shift = At.Shift.AFTER))
    private static void fixPaintingPlacement(StructurePlaceSettings placementIn, Vec3 vec3, ServerLevelAccessor level, CompoundTag compoundtag, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof Painting painting)) {
            return;
        }

        var pos = new BlockPos.MutableBlockPos();
        pos.set(painting.getPos());
        var variant = painting.getVariant().value();

        var width = variant.getWidth() / 16;
        var height = variant.getHeight() / 16;
        var direction = painting.getDirection();

        // paintings with an even height seem to always be moved upwards...
        if (height % 2 == 0) {
            pos.move(0, -1, 0);
        }

        // paintings with an even width seem to be moved in the clockwise direction of their facing direction,
        // if they're west or south.
        if (width % 2 == 0 && (direction == Direction.WEST || direction == Direction.SOUTH)) {
            var moveTo = direction.getClockWise().getNormal();
            pos.move(moveTo);
        }

        painting.setPos(pos.getCenter());
    }
} 