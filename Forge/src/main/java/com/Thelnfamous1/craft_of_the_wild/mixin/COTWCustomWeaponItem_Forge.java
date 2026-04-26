package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.init.COTWCustomWeaponItem;
import com.Thelnfamous1.craft_of_the_wild.item.ExtendedReach;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(COTWCustomWeaponItem.class)
public abstract class COTWCustomWeaponItem_Forge extends Item implements ExtendedReach {
    public COTWCustomWeaponItem_Forge(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Optional<Attribute> getAttackReachAttribute() {
        return Optional.of(ForgeMod.ENTITY_REACH.get());
    }
}
