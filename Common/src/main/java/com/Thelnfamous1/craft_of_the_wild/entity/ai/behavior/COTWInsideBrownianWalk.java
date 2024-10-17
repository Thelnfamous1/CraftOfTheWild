package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWInsideBrownianWalk<E extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
           Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
   private float speedModifier = 1.0F;

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   public COTWInsideBrownianWalk<E> speedModifier(float speedModifier){
      this.speedModifier = speedModifier;
      return this;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (level.canSeeSky(entity.blockPosition())) {
         return false;
      } else {
         BlockPos entityBlockPos = entity.blockPosition();
         List<BlockPos> nearbyBlockPos = BlockPos.betweenClosedStream(entityBlockPos.offset(-1, -1, -1), entityBlockPos.offset(1, 1, 1))
                 .map(BlockPos::immutable)
                 .collect(Collectors.toList());
         Collections.shuffle(nearbyBlockPos);
         nearbyBlockPos.stream()
                 .filter((bp) -> !level.canSeeSky(bp))
                 .filter((bp) -> level.loadedAndEntityCanStandOn(bp, entity))
                 .filter((bp) -> level.noCollision(entity))
                 .findFirst()
                 .ifPresent((walkTarget) -> BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(walkTarget, this.speedModifier, 0)));
         return true;
      }
   }
}