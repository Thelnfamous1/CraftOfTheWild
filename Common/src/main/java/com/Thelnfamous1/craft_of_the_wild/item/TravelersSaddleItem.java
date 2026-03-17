package com.Thelnfamous1.craft_of_the_wild.item;

import com.Thelnfamous1.craft_of_the_wild.duck.SaddleEquipper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.level.gameevent.GameEvent;

public class TravelersSaddleItem extends SaddleItem {
    public TravelersSaddleItem(Properties $$0) {
        super($$0);
    }

    public static boolean is(ItemStack stack){
        return stack.getItem() instanceof TravelersSaddleItem;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pTarget, InteractionHand pHand) {
        if (pTarget instanceof SaddleEquipper saddleable && pTarget.isAlive()) {
            if (!saddleable.isSaddled() && saddleable.isSaddleable()) {
                if (!pPlayer.level().isClientSide) {
                    saddleable.craft_of_the_wild$equipSaddle(SoundSource.NEUTRAL, pStack.split(1));
                    pTarget.level().gameEvent(pTarget, GameEvent.EQUIP, pTarget.position());
                    //pStack.shrink(1);
                }

                return InteractionResult.sidedSuccess(pPlayer.level().isClientSide);
            }
        }

        return InteractionResult.PASS;
    }
}
