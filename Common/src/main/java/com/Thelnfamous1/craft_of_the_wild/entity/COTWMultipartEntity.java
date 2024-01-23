package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface COTWMultipartEntity {

    static List<Entity> partEntityList(COTWMultipartEntity mpe){
        return Collections.unmodifiableList(Arrays.asList(mpe.getPartEntities()));
    }

    boolean hurt(Entity partEntity, DamageSource pSource, float pAmount);

    Entity[] getPartEntities();
}
