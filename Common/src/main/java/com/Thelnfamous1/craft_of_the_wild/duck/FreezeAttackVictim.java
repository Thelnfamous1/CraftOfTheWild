package com.Thelnfamous1.craft_of_the_wild.duck;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import net.minecraft.world.entity.Entity;

public interface FreezeAttackVictim {

    String PAUSE_FROZEN_TICKS = "PauseFrozenTicks";

    static void applyFreezeEffect(Entity target, int frozenTicksToAdd, int pauseFrozenTicks) {
        target.setTicksFrozen(target.getTicksFrozen() + frozenTicksToAdd);
        ((FreezeAttackVictim) target).craft_of_the_wild$setPauseFrozenTicks(pauseFrozenTicks);
        COTWCommon.debug(Constants.DEBUG_PAUSE_FROZEN_TICKS, "Frozen ticks have been paused in FreezeAttackVictim for {} with {} ticks frozen", target, target.getTicksFrozen());
    }

    int craft_of_the_wild$getPauseFrozenTicks();

    void craft_of_the_wild$setPauseFrozenTicks(int pauseFrozenTicks);

}
