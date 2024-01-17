package com.Thelnfamous1.craft_of_the_wild.entity.ai;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;
import java.util.function.Consumer;

public class Emerging<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.IS_EMERGING, MemoryStatus.VALUE_PRESENT),
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED)
    );
    private Consumer<E> startEmerging = e -> {
        e.setPose(Pose.EMERGING);
        e.playSound(SoundEvents.WARDEN_EMERGE, 5.0F, 1.0F);
    };
    private Consumer<E> finishEmerging = e -> {
        if (e.hasPose(Pose.EMERGING)) {
            e.setPose(Pose.STANDING);
        }
    };

    public Emerging(int emergeTicks) {
        this.runFor(e -> emergeTicks);
    }

    public Emerging<E> startEmerging(Consumer<E> startEmerging){
        this.startEmerging = startEmerging;
        return this;
    }

    public Emerging<E> finishEmerging(Consumer<E> finishEmerging){
        this.finishEmerging = finishEmerging;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return true;
    }

    @Override
    protected void start(E entity) {
        this.startEmerging.accept(entity);
    }

    @Override
    protected void stop(E entity) {
        this.finishEmerging.accept(entity);
    }
}