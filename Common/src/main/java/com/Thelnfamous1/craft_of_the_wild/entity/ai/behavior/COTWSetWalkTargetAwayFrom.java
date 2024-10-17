package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWSetWalkTargetAwayFrom<E extends PathfinderMob, T> extends ExtendedBehaviour<E> {
   private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
           Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED));

   protected MemoryModuleType<T> walkTargetAwayFromMemory;
   protected float speedModifier = 1.0F;
   protected double desiredDistance = 3.0D;
   protected boolean hasTarget;
   protected Function<T, Vec3> toPosition;

   public static COTWSetWalkTargetAwayFrom<PathfinderMob, BlockPos> pos(MemoryModuleType<BlockPos> pWalkTargetAwayFromMemory, float pSpeedModifier, int pDesiredDistance, boolean pHasTarget) {
      return new COTWSetWalkTargetAwayFrom<>(pWalkTargetAwayFromMemory, Vec3::atBottomCenterOf).speedModifer(pSpeedModifier).desiredDistance(pDesiredDistance).hasTarget(pHasTarget);
   }

   public static COTWSetWalkTargetAwayFrom<PathfinderMob, ? extends Entity> entity(MemoryModuleType<? extends Entity> pWalkTargetAwayFromMemory, float pSpeedModifier, int pDesiredDistance, boolean pHasTarget) {
      return new COTWSetWalkTargetAwayFrom<>(pWalkTargetAwayFromMemory, Entity::position).speedModifer(pSpeedModifier).desiredDistance(pDesiredDistance).hasTarget(pHasTarget);
   }

   public COTWSetWalkTargetAwayFrom(MemoryModuleType<T> walkTargetAwayFromMemory, Function<T, Vec3> toPosition){
      this.walkTargetAwayFromMemory = walkTargetAwayFromMemory;
      this.toPosition = toPosition;
   }

   public COTWSetWalkTargetAwayFrom<E, T> speedModifer(float speedModifer){
      this.speedModifier = speedModifer;
      return this;
   }

   public COTWSetWalkTargetAwayFrom<E, T> desiredDistance(double desiredDistance){
      this.desiredDistance = desiredDistance;
      return this;
   }

   public COTWSetWalkTargetAwayFrom<E, T> hasTarget(boolean hasTarget){
      this.hasTarget = hasTarget;
      return this;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      Optional<WalkTarget> existingWalkTarget = COTWUtil.getOptionalMemory(entity, MemoryModuleType.WALK_TARGET);
      if (existingWalkTarget.isPresent() && !this.hasTarget) {
         return false;
      } else {
         Vec3 entityPos = entity.position();
         Optional<T> walkTargetAwayFromMemory = COTWUtil.getOptionalMemory(entity, this.walkTargetAwayFromMemory);
         if(walkTargetAwayFromMemory.isEmpty()){
            return false;
         }
         Vec3 avoidPos = this.toPosition.apply(walkTargetAwayFromMemory.get());
         if (!entityPos.closerThan(avoidPos, this.desiredDistance)) {
            return false;
         } else {
            if (existingWalkTarget.isPresent() && existingWalkTarget.get().getSpeedModifier() == this.speedModifier) {
               Vec3 walkTargetDiff = existingWalkTarget.get().getTarget().currentPosition().subtract(entityPos);
               Vec3 avoidPosDiff = avoidPos.subtract(entityPos);
               if (walkTargetDiff.dot(avoidPosDiff) < 0.0D) {
                  return false;
               }
            }

            for(int i = 0; i < 10; ++i) {
               Vec3 awayPos = LandRandomPos.getPosAway(entity, 16, 7, avoidPos);
               if (awayPos != null) {
                  BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(awayPos, this.speedModifier, 0));
                  break;
               }
            }

            return true;
         }
      }
   }

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }
}