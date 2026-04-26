package com.Thelnfamous1.craft_of_the_wild.item;

import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Optional;

public interface ExtendedReach {
    default Optional<Attribute> getAttackReachAttribute(){
        return Optional.empty();
    }
}
