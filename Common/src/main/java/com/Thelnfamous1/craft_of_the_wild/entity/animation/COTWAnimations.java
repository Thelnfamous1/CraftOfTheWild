package com.Thelnfamous1.craft_of_the_wild.entity.animation;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class COTWAnimations {

    public static RawAnimation WALK = RawAnimation.begin().thenLoop("walk"); // continuous animation
    public static RawAnimation IDLE = RawAnimation.begin().thenLoop("idle"); // continuous animation

    public static RawAnimation SPAWN = RawAnimation.begin().thenPlayAndHold("spawn"); // transitional animation

    public static RawAnimation DESPAWN = RawAnimation.begin().thenPlayAndHold("despawn"); // transitional animation

    public static RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep"); // continuous animation

    public static RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death"); // transitional animation
    public static RawAnimation STONE_TALUS_POUND = RawAnimation.begin().thenPlay("Move 1"); // one-shot animation
    public static RawAnimation STONE_TALUS_THROW = RawAnimation.begin().thenPlay("Move 2"); // one-shot animation
    public static RawAnimation STONE_TALUS_HEADBUTT = RawAnimation.begin().thenPlay("Move 3"); // one-shot animation
    public static RawAnimation STONE_TALUS_PUNCH = RawAnimation.begin().thenPlay("Move 4"); // one-shot animation

    public static AnimationController<StoneTalus> moveController(StoneTalus talus) {
        return new AnimationController<>(talus, "Move", 10, state -> {
            if(!talus.refuseToMove(true)){
                if (talus.isWalking()) {
                    return state.setAndContinue(WALK);
                } else{
                    return state.setAndContinue(IDLE);
                }
            }
            return PlayState.STOP;
        });
    }

    public static AnimationController<StoneTalus> poseController(StoneTalus talus) {
        return new AnimationController<>(talus, "Pose", 0, state -> {
            if (talus.hasPose(Pose.DIGGING)) {
                talus.clientDiggingParticles(state);
                return state.setAndContinue(DESPAWN);
            } else if (talus.hasPose(Pose.EMERGING)) {
                talus.clientDiggingParticles(state);
                return state.setAndContinue(SPAWN);
            } else if (talus.hasPose(Pose.SLEEPING)) {
                return state.setAndContinue(SLEEP);
            } else if(talus.hasPose(Pose.DYING)){
                return state.setAndContinue(DEATH);
            }
            return PlayState.STOP;
        });
    }

    public static AnimationController<StoneTalus> attackController(StoneTalus talus) {
        return new AnimationController<>(talus, "Attack", 0, state -> {
            if (!talus.refuseToMove(false) && talus.isAttackAnimationInProgress()){
                //noinspection ConstantConditions
                switch (talus.getCurrentAttackType()){
                    case POUND -> {
                        return state.setAndContinue(STONE_TALUS_POUND);
                    }
                    case THROW -> {
                        return state.setAndContinue(STONE_TALUS_THROW);
                    }
                    case HEADBUTT -> {
                        return state.setAndContinue(STONE_TALUS_HEADBUTT);
                    }
                    case PUNCH -> {
                        return state.setAndContinue(STONE_TALUS_PUNCH);
                    }
                }
            }

            state.resetCurrentAnimation();

            return PlayState.STOP;
        });
    }

}
