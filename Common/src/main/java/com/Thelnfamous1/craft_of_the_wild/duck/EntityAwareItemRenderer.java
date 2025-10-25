package com.Thelnfamous1.craft_of_the_wild.duck;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface EntityAwareItemRenderer {

    void craft_of_the_wild$setCurrentEntity(@Nullable LivingEntity entity);

    @Nullable LivingEntity craft_of_the_wild$getCurrentEntity();
}
