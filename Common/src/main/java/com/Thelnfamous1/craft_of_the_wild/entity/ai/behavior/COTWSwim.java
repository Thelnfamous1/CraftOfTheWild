package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;

public class COTWSwim<T extends Mob> extends ExtendedBehaviour<T> {
    protected float chance = 0.8F;

    public COTWSwim<T> jumpChance(float chance){
        this.chance = chance;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of();
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T entity) {
        return entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold() || entity.isInLava();
    }

    @Override
    protected boolean shouldKeepRunning(T mob) {
        return this.checkExtraStartConditions((ServerLevel)mob.level(), mob);
    }

    @Override
    protected void tick(T entity) {
        if (entity.getRandom().nextFloat() < this.chance) {
            entity.getJumpControl().jump();
        }
    }
}
