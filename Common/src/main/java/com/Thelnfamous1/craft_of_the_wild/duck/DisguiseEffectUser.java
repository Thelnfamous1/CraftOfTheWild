package com.Thelnfamous1.craft_of_the_wild.duck;

import com.Thelnfamous1.craft_of_the_wild.init.MobEffectInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface DisguiseEffectUser {
    int DEFAULT_DISGUISE_COOLDOWN_TICKS = COTWUtil.secondsToTicks(5);

    int craft_of_the_wild$getDisguiseEffectCooldownTicks(MobEffect disguiseEffect);

    void craft_of_the_wild$setDisguiseEffectCooldownTicks(MobEffect disguiseEffect, int disguiseCooldownTicks);

    default boolean craft_of_the_wild$canApplyDisguiseEffect(MobEffect disguiseEffect){
         return this.craft_of_the_wild$getDisguiseEffectCooldownTicks(disguiseEffect) <= 0;
     }

     default void craft_of_the_wild$cooldownDisguiseEffect(MobEffect mobEffect){
        this.craft_of_the_wild$setDisguiseEffectCooldownTicks(mobEffect, DEFAULT_DISGUISE_COOLDOWN_TICKS);
     }

     @Nullable
     default MobEffect craft_of_the_wild$getAffectingDisguiseEffect(Entity target){
         return target.getType().is(COTWTags.RADIANT_DISGUISE_AFFECTS) ?
                 MobEffectInit.RADIANT_DISGUISE.get() :
                 target.getType().is(COTWTags.BOKOLBIN_DISGUISE_AFFECTS) ?
                         MobEffectInit.BOKOBLIN_DISGUISE.get() :
                         null;
     }

    void craft_of_the_wild$syncDisguiseEffectCooldowns(Map<MobEffect, Integer> disguiseEffectCooldowns);

    void craft_of_the_wild$removeDisguiseEffect(MobEffect disguiseEffect);
}
