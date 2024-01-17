package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface MultipartEntity {
    boolean hurt(Entity partEntity, DamageSource pSource, float pAmount);

    Entity[] getSubEntities();
}
