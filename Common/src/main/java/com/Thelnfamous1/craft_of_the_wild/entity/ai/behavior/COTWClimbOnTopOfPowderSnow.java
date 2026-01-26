package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;

public class COTWClimbOnTopOfPowderSnow extends ExtendedBehaviour<Mob> {

   public COTWClimbOnTopOfPowderSnow() {
      super();
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel pLevel, Mob mob) {
      boolean inPowderSnow = mob.wasInPowderSnow || mob.isInPowderSnow;
      if (inPowderSnow && mob.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
         BlockPos blockpos = mob.blockPosition().above();
         BlockState blockstate = pLevel.getBlockState(blockpos);
         return blockstate.is(Blocks.POWDER_SNOW) || blockstate.getCollisionShape(pLevel, blockpos) == Shapes.empty();
      } else {
         return false;
      }
   }

   @Override
   protected boolean shouldKeepRunning(Mob entity) {
      return this.checkExtraStartConditions((ServerLevel) entity.level(), entity);
   }

   @Override
   protected void tick(Mob pOwner) {
      pOwner.getJumpControl().jump();
   }

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return List.of();
   }
}