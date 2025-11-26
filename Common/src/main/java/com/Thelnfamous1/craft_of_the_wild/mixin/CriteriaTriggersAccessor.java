package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CriteriaTriggers.class)
public interface CriteriaTriggersAccessor {

    @Invoker("register")
    static <T extends CriterionTrigger<?>> T craft_of_the_wild$callRegister(T $$0) {
        throw new AssertionError("CriteriaTriggersAccessor not applied!");
    }
}
