package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.duck.SaddleEquipper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DispenseItemBehaviorInit {
    public static void bootstrap(){
        DispenserBlock.registerBehavior(ItemInit.TRAVELERS_SADDLE.get(), new OptionalDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                List<LivingEntity> list = blockSource.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), (entity) -> {
                    if (!(entity instanceof SaddleEquipper saddleable)) {
                        return false;
                    } else {
                        return !saddleable.isSaddled() && saddleable.isSaddleable();
                    }
                });
                if (!list.isEmpty()) {
                    ((SaddleEquipper)list.get(0)).craft_of_the_wild$equipSaddle(SoundSource.BLOCKS, stack.split(1));
                    this.setSuccess(true);
                    return stack;
                } else {
                    return super.execute(blockSource, stack);
                }
            }
        });
    }
}
