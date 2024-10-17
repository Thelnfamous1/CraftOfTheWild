package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import java.util.List;
import java.util.Optional;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWSetWalkTargetToPoi<E extends PathfinderMob> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));

    protected MemoryModuleType<GlobalPos> poiPosMemory = MemoryModuleType.HOME;
    protected float speedModifier = 1.0F;
    protected int closeEnough;
    protected double maxDistFromPoi;

    public COTWSetWalkTargetToPoi(int closeEnough, double maxDistFromPoi){
        this.closeEnough = closeEnough;
        this.maxDistFromPoi = maxDistFromPoi;
    }

    public COTWSetWalkTargetToPoi<E> speedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        GlobalPos poiPos = BrainUtils.getMemory(entity, this.poiPosMemory);
        if(poiPos == null) return false;

        Optional<Long> crwts = COTWUtil.getOptionalMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        if (poiPos.dimension() == level.dimension() && (crwts.isEmpty() || level.getGameTime() - crwts.get() <= 1200L)) {
            if (poiPos.pos().distManhattan(entity.blockPosition()) > this.maxDistFromPoi) {
                Vec3 walkTarget = null;
                int tries = 0;

                while(walkTarget == null || BlockPos.containing(walkTarget).distManhattan(entity.blockPosition()) > this.maxDistFromPoi) {
                    walkTarget = DefaultRandomPos.getPosTowards(entity, 15, 7, Vec3.atBottomCenterOf(poiPos.pos()), Mth.HALF_PI);
                    ++tries;
                    if (tries == 1000) {
                        //entity.releasePoi(poiPos);
                        BrainUtils.clearMemory(entity, this.poiPosMemory);
                        BrainUtils.setMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, level.getGameTime());
                        return true;
                    }
                }

                BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(walkTarget, this.speedModifier, this.closeEnough));
            } else if (poiPos.pos().distManhattan(entity.blockPosition()) > this.closeEnough) {
                BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(poiPos.pos(), this.speedModifier, this.closeEnough));
            }
        } else {
            //entity.releasePoi(poiPos);
            BrainUtils.clearMemory(entity, this.poiPosMemory);
            BrainUtils.setMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, level.getGameTime());
        }

        return true;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}