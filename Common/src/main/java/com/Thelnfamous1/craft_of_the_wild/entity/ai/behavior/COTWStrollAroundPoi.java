package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWStrollAroundPoi<E extends PathfinderMob> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
            Pair.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
    private static final int MIN_TIME_BETWEEN_STROLLS = 180;
    private static final int STROLL_MAX_XZ_DIST = 8;
    private static final int STROLL_MAX_Y_DIST = 6;

    protected MemoryModuleType<GlobalPos> poiPosMemory = MemoryModuleType.HOME;
    protected float speedModifier = 1.0F;
    protected int closeEnough;
    protected double maxDistFromPoi;
    protected long lastCheck;

    public COTWStrollAroundPoi(int closeEnough) {
        this.closeEnough = closeEnough;
    }

    public COTWStrollAroundPoi<E> speedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        GlobalPos poiPos = BrainUtils.getMemory(entity, this.poiPosMemory);
        if(poiPos == null) return false;

        if (level.dimension() == poiPos.dimension() && poiPos.pos().closerToCenterThan(entity.position(), this.maxDistFromPoi)) {
            if (level.getGameTime() > this.lastCheck) {
                Optional<Vec3> landRandomPos = Optional.ofNullable(LandRandomPos.getPos(entity, STROLL_MAX_XZ_DIST, STROLL_MAX_Y_DIST));
                landRandomPos.map((lrp) -> new WalkTarget(lrp, this.speedModifier, this.closeEnough))
                        .ifPresentOrElse(
                                wt -> BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, wt),
                                () -> BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET));
                this.lastCheck = level.getGameTime() + MIN_TIME_BETWEEN_STROLLS;
            }
            return true;
        } else {
            return false;
        }
    }
}