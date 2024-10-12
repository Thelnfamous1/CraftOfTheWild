package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class COTWStrollToPoi<E extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
           Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED));

   protected MemoryModuleType<GlobalPos> poiPosMemory = MemoryModuleType.HOME;
   protected float speedModifier = 1.0F;
   protected int closeEnough;
   protected double maxDistFromPoi;
   protected long lastCheck;

   public COTWStrollToPoi(int closeEnough, double maxDistFromPoi){
       this.closeEnough = closeEnough;
       this.maxDistFromPoi = maxDistFromPoi;
   }

    public COTWStrollToPoi<E> speedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
        return this;
    }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      GlobalPos poiPos = BrainUtils.getMemory(entity, this.poiPosMemory);
      if(poiPos == null) return false;

      if (level.dimension() == poiPos.dimension() && poiPos.pos().closerToCenterThan(entity.position(), this.maxDistFromPoi)) {
          if (level.getGameTime() > this.lastCheck) {
              BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(poiPos.pos(), this.speedModifier, this.closeEnough));
              this.lastCheck = level.getGameTime() + 80L;
          }
          return true;
      } else {
         return false;
      }
   }

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }
}