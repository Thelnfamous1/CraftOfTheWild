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
    public static RawAnimation STONE_TALUS_POUND = RawAnimation.begin().thenPlay("Move 1");
    public static RawAnimation STONE_TALUS_THROW = RawAnimation.begin().thenPlay("Move 2");
    public static RawAnimation STONE_TALUS_HEADBUTT = RawAnimation.begin().thenPlay("Move 3");
    public static RawAnimation STONE_TALUS_PUNCH = RawAnimation.begin().thenPlay("Move 4");

    public static AnimationController<StoneTalus> moveController(StoneTalus talus) {
        return new AnimationController<>(talus, "Move", 0, state -> {
            if(!talus.refuseToMove()){
                if (state.isMoving()) {
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
            if (talus.isInsideGround()) {
                if (talus.getPose() == Pose.DIGGING) {
                    talus.clientDiggingParticles(state);
                    return state.setAndContinue(DESPAWN);
                } else if (talus.getPose() == Pose.EMERGING) {
                    talus.clientDiggingParticles(state);
                    return state.setAndContinue(SPAWN);
                } else if (talus.getPose() == Pose.SLEEPING) {
                    return state.setAndContinue(SLEEP);
                }
            }
            return PlayState.STOP;
        });
    }

    public static AnimationController<StoneTalus> attackController(StoneTalus talus) {
        return new AnimationController<>(talus, "Attack", 0, state -> {
            if (talus.isAttackAnimationInProgress()){
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
