package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWInteractWith<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
            Pair.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
    protected MemoryModuleType<LivingEntity> interactionMemory = MemoryModuleType.INTERACTION_TARGET;
    protected float speedModifier = 1.0F;
    protected float distanceSqr;
    protected int closeEnough;
    protected Predicate<E> canInteract = le -> true;
    protected BiPredicate<E, LivingEntity> canInteractWith = (le, le1) -> le.getType().equals(le1.getType());

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    public COTWInteractWith(float distance, int closeEnough){
        this.distanceSqr = Mth.square(distance);
        this.closeEnough = closeEnough;
    }

    public COTWInteractWith<E> canInteract(Predicate<E> canInteract) {
        this.canInteract = canInteract;
        return this;
    }

    public COTWInteractWith<E> canInteractWith(BiPredicate<E, LivingEntity> canInteractWith) {
        this.canInteractWith = canInteractWith;
        return this;
    }

    public COTWInteractWith<E> speedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
        return this;
    }

    public COTWInteractWith<E> distance(float distance) {
        this.distanceSqr = Mth.square(distance);
        return this;
    }

    public COTWInteractWith<E> closeEnough(int closeEnough) {
        this.closeEnough = closeEnough;
        return this;
    }

    public COTWInteractWith<E> interactionMemory(MemoryModuleType<LivingEntity> interactionMemory) {
        this.interactionMemory = interactionMemory;
        return this;
    }


    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        NearestVisibleLivingEntities nvle = BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if (this.canInteract.test(entity) && nvle.contains(le -> this.canInteractWith.test(entity, le))) {
            Optional<LivingEntity> interactTarget = nvle.findClosest((le) -> le.distanceToSqr(entity) <= (double)this.distanceSqr && this.canInteractWith.test(entity, le));
            interactTarget.ifPresent((le) -> {
                BrainUtils.setMemory(entity, this.interactionMemory, le);
                BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(le, true));
                BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(le, false), this.speedModifier, this.closeEnough));
            });
            return true;
        } else {
            return false;
        }
    }
}