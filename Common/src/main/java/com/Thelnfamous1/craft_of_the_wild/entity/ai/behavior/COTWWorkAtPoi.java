package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWWorkAtPoi<E extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final int CHECK_COOLDOWN = 300;
   private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
           Pair.of(MemoryModuleType.LAST_WORKED_AT_POI, MemoryStatus.REGISTERED),
           Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));

   protected MemoryModuleType<GlobalPos> poiPosMemory = MemoryModuleType.HOME;
   private long lastCheck;
   protected Consumer<E> workCallback = e -> {};
   protected double closeEnough = 1.73D;

   public COTWWorkAtPoi() {
   }

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   public COTWWorkAtPoi<E> work(Consumer<E> workCallback){
      this.workCallback = workCallback;
      return this;
   }

   public COTWWorkAtPoi<E> closeEnough(int closeEnough){
      this.closeEnough = closeEnough;
      return this;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel pLevel, E pOwner) {
      if (pLevel.getGameTime() - this.lastCheck < CHECK_COOLDOWN) {
         return false;
      } else if (pLevel.random.nextInt(2) != 0) {
         return false;
      } else {
         this.lastCheck = pLevel.getGameTime();
         GlobalPos poiPos = BrainUtils.getMemory(pOwner, this.poiPosMemory);
         if(poiPos == null) return false;
         return poiPos.dimension() == pLevel.dimension() && poiPos.pos().closerToCenterThan(pOwner.position(), this.closeEnough);
      }
   }

   @Override
   protected void start(E pEntity) {
      BrainUtils.setMemory(pEntity, MemoryModuleType.LAST_WORKED_AT_POI, pEntity.level().getGameTime());
      COTWUtil.getOptionalMemory(pEntity, this.poiPosMemory).ifPresent((p_24821_) -> {
         BrainUtils.setMemory(pEntity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(p_24821_.pos()));
      });
      this.workCallback.accept(pEntity);

   }

   @Override
   protected boolean shouldKeepRunning(E pEntity) {
      Optional<GlobalPos> poiPosMemory = COTWUtil.getOptionalMemory(pEntity, this.poiPosMemory);
      if (poiPosMemory.isEmpty()) {
         return false;
      } else {
         GlobalPos poiPos = poiPosMemory.get();
         return poiPos.dimension() == pEntity.level().dimension() && poiPos.pos().closerToCenterThan(pEntity.position(), this.closeEnough);
      }
   }
}