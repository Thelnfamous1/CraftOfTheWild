package com.Thelnfamous1.craft_of_the_wild.entity.animation;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class COTWAnimations {

    public static RawAnimation STONE_TALUS_POUND = RawAnimation.begin().thenPlay("Move 1");
    public static RawAnimation STONE_TALUS_THROW = RawAnimation.begin().thenPlay("Move 2");
    public static RawAnimation STONE_TALUS_HEADBUTT = RawAnimation.begin().thenPlay("Move 3");
    public static RawAnimation STONE_TALUS_PUNCH = RawAnimation.begin().thenPlay("Move 4");

    public static AnimationController<StoneTalus> stoneTalus(StoneTalus stoneTalus) {
        return new AnimationController<>(stoneTalus, "Attack", 0, state -> {
            if (stoneTalus.isAttackAnimationInProgress()){
                switch (stoneTalus.getCurrentAttackType()){
                    case POUND -> state.setAndContinue(STONE_TALUS_POUND);
                    case THROW -> state.setAndContinue(STONE_TALUS_THROW);
                    case HEADBUTT -> state.setAndContinue(STONE_TALUS_HEADBUTT);
                    case PUNCH -> state.setAndContinue(STONE_TALUS_PUNCH);
                }
            }

            state.getController().forceAnimationReset();

            return PlayState.STOP;
        });
    }

}
