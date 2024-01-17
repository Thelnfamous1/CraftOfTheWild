package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("ENTITY_COUNTER")
    static AtomicInteger craft_of_the_wild$getENTITY_COUNTER(){
        throw new AssertionError();
    }
}
