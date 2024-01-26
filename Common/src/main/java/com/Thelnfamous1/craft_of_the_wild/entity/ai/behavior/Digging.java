package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Digging<E extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
           ObjectArrayList.of(
                   Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT),
                   Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT)
           );

   private Consumer<E> startDigging = e -> {
      if (e.onGround()) {
         e.setPose(Pose.DIGGING);
         e.playSound(SoundEvents.WARDEN_DIG, 5.0F, 1.0F);
      } else {
         e.playSound(SoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
         this.stop(e);
      }
   };
   private Consumer<E> finishDigging = e -> {
      if (e.getRemovalReason() == null) {
         e.remove(Entity.RemovalReason.DISCARDED);
      }
   };
   private Predicate<E> continueDigging = e -> e.getRemovalReason() == null;
   private Predicate<E> canDig = e -> e.onGround() || e.isInWater() || e.isInLava();

   public Digging(int pDuration) {
      this.runFor(e -> pDuration);
   }

   public Digging<E> canDig(Predicate<E> canDig){
      this.canDig = canDig;
      return this;
   }

   public Digging<E> startDigging(Consumer<E> startDigging){
      this.startDigging = startDigging;
      return this;
   }

   public Digging<E> continueDigging(Predicate<E> continueDigging){
      this.continueDigging = continueDigging;
      return this;
   }

   public Digging<E> finishDigging(Consumer<E> finishDigging){
      this.finishDigging = finishDigging;
      return this;
   }

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel pLevel, E pOwner) {
      return this.canDig.test(pOwner);
   }

   @Override
   protected void start(E pEntity) {
      this.startDigging.accept(pEntity);
   }

   @Override
   protected boolean shouldKeepRunning(E entity) {
      return this.continueDigging.test(entity);
   }

   @Override
   protected void stop(E pEntity) {
      this.finishDigging.accept(pEntity);
   }
}