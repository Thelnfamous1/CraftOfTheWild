package com.Thelnfamous1.craft_of_the_wild.entity.talus;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusLike;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

public class StoneTalusBodyRotationControl<T extends Mob & StoneTalusLike> extends BodyRotationControl {
    private final T stoneTalus;

    public StoneTalusBodyRotationControl(T stoneTalus) {
        super(stoneTalus);
        this.stoneTalus = stoneTalus;
    }

    @Override
    public void clientTick() {
        if (!this.stoneTalus.refuseToMove(false)) {
            super.clientTick();
        }

    }
}