package com.Thelnfamous1.craft_of_the_wild.entity.animation;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class COTWAnimations {

    public static RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    public static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    public static RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");

    public static RawAnimation DESPAWN = RawAnimation.begin().thenPlay("despawn");

    public static RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");

    public static RawAnimation DEATH = RawAnimation.begin().thenPlay("death");
    public static RawAnimation STONE_TALUS_POUND = RawAnimation.begin().thenPlay("Move 1");
    public static RawAnimation STONE_TALUS_THROW = RawAnimation.begin().thenPlay("Move 2");
    public static RawAnimation STONE_TALUS_HEADBUTT = RawAnimation.begin().thenPlay("Move 3");
    public static RawAnimation STONE_TALUS_PUNCH = RawAnimation.begin().thenPlay("Move 4");

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
