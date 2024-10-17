package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;

public class COTWWakeUp<E extends LivingEntity> extends ExtendedBehaviour<E> {

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return List.of();
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (!entity.getBrain().isActive(Activity.REST) && entity.isSleeping()) {
         entity.stopSleeping();
         return true;
      } else {
         return false;
      }
   }
}