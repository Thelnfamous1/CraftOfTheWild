package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.world.entity.ai.control.BodyRotationControl;

public class StoneTalusBodyRotationControl<T extends StoneTalus> extends BodyRotationControl {
    private final T stoneTalus;

    public StoneTalusBodyRotationControl(T stoneTalus) {
        super(stoneTalus);
        this.stoneTalus = stoneTalus;
    }

    @Override
    public void clientTick() {
        if (!this.stoneTalus.refuseToMove()) {
            super.clientTick();
        }

    }
}