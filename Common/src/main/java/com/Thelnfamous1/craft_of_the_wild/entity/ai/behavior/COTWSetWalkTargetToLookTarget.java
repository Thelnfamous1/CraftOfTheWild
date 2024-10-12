package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWSetWalkTargetToLookTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT));
    protected Predicate<E> canUse = le -> true;
    protected Function<E, Float> speedModifier = le -> 1.0F;
    protected int closeEnough;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    public COTWSetWalkTargetToLookTarget(int closeEnough) {
        this.closeEnough = closeEnough;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        if (!this.canUse.test(entity)) {
            return false;
        } else {
            BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(BrainUtils.getMemory(entity, MemoryModuleType.LOOK_TARGET), this.speedModifier.apply(entity), this.closeEnough));
            return true;
        }
    }
}