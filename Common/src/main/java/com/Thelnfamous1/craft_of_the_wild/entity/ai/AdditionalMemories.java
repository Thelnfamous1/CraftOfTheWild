package com.Thelnfamous1.craft_of_the_wild.entity.ai;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;
import java.util.Objects;

/**
 * Utility behavior used to register additional memories.
 * This is usually only necessary if Activity memory requirements don't have their required memories already registered by other behaviors.
 */
public final class AdditionalMemories<E extends LivingEntity> extends ExtendedBehaviour<E> {

    private final List<Pair<MemoryModuleType<?>, MemoryStatus>> additionalMemories;

    /**
     * @param additionalMemories Additional memories to be registered to the brain.
     */
    public AdditionalMemories(MemoryModuleType<?>... additionalMemories){
        this.additionalMemories = Util.make(new ObjectArrayList<>(), list -> {
            for(MemoryModuleType<?> additionalMemory : additionalMemories){
                list.add(Pair.of(additionalMemory, MemoryStatus.REGISTERED));
                this.entryCondition.put(additionalMemory, MemoryStatus.REGISTERED);
            }
        });
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return Objects.requireNonNullElseGet(this.additionalMemories, List::of);
    }

    /**
     * Not an actual behavior, so never should be running.
     */
    @Override
    protected boolean doStartCheck(ServerLevel level, E entity, long gameTime) {
        return false;
    }

    /**
     * Not an actual behavior, so never should be running.
     */
    @Override
    public Status getStatus() {
        return Status.STOPPED;
    }
}
