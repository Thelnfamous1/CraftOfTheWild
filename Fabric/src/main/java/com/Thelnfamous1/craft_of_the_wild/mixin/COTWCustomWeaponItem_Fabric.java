package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.init.COTWCustomWeaponItem;
import com.Thelnfamous1.craft_of_the_wild.item.ExtendedReach;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(COTWCustomWeaponItem.class)
public abstract class COTWCustomWeaponItem_Fabric extends Item implements ExtendedReach {
    public COTWCustomWeaponItem_Fabric(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Optional<Attribute> getAttackReachAttribute() {
        return Optional.of(ReachEntityAttributes.ATTACK_RANGE);
    }
}
