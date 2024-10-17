package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Consumer;

public class COTWVillagerCalmDown<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.HURT_BY, MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.REGISTERED)
            /*Pair.of(MemoryModuleType.NEAREST_HOSTILE, MemoryStatus.REGISTERED)*/);
    private static final int SAFE_DISTANCE_FROM_DANGER = 36;
    protected Consumer<E> whenCalmingDown;

    public COTWVillagerCalmDown() {
    }

    public COTWVillagerCalmDown<E> whenCalmingDown(Consumer<E> whenCalmingDown){
        this.whenCalmingDown = whenCalmingDown;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        boolean shouldPanic = COTWUtil.getOptionalMemory(entity, MemoryModuleType.HURT_BY).isPresent() ||
                COTWUtil.getOptionalMemory(entity, MemoryModuleType.HURT_BY_ENTITY)
                        .filter((attacker) -> attacker.distanceToSqr(entity) <= SAFE_DISTANCE_FROM_DANGER)
                        .isPresent();
        if (!shouldPanic) {
            BrainUtils.clearMemory(entity, MemoryModuleType.HURT_BY);
            BrainUtils.clearMemory(entity, MemoryModuleType.HURT_BY_ENTITY);
            this.whenCalmingDown.accept(entity);
        }

        return true;
    }
}