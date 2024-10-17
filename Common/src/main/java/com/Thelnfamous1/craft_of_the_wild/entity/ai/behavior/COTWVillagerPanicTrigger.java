package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Consumer;

public class COTWVillagerPanicTrigger<E extends LivingEntity> extends ExtendedBehaviour<E> {

   protected Consumer<E> whenPanicking;

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return List.of();
   }

   public COTWVillagerPanicTrigger<E> whenPanicking(Consumer<E> whenPanicking){
      this.whenPanicking = whenPanicking;
      return this;
   }

   @Override
   protected boolean shouldKeepRunning(E entity) {
      return isHurt(entity) || hasHostile(entity);
   }

   @Override
   protected void start(E pEntity) {
      if (isHurt(pEntity) || hasHostile(pEntity)) {
         Brain<?> brain = pEntity.getBrain();
         if (!brain.isActive(Activity.PANIC)) {
            brain.eraseMemory(MemoryModuleType.PATH);
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
            brain.eraseMemory(MemoryModuleType.BREED_TARGET);
            brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
         }

         brain.setActiveActivityIfPossible(Activity.PANIC);
         if(brain.isActive(Activity.PANIC)){
            this.whenPanicking.accept(pEntity);
         }
      }

   }

   public static boolean hasHostile(LivingEntity pEntity) {
      return BrainUtils.hasMemory(pEntity, MemoryModuleType.NEAREST_HOSTILE);
   }

   public static boolean isHurt(LivingEntity pEntity) {
      return  BrainUtils.hasMemory(pEntity, MemoryModuleType.HURT_BY);
   }
}