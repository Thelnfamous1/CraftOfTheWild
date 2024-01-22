package com.Thelnfamous1.craft_of_the_wild.entity.ai;

import com.Thelnfamous1.craft_of_the_wild.entity.AnimatedAttacker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class AttackingLookController<T extends Mob & AnimatedAttacker<A>, A extends AnimatedAttacker.AttackType> extends LookControl {
    private final T attacker;

    public AttackingLookController(T attacker) {
        super(attacker);
        this.attacker = attacker;
    }

    @Override
    public void tick() {
        if(!this.attacker.isAttackAnimationInProgress() || this.attacker.canRotateDuringAttack(this.attacker.getCurrentAttackType())){
            super.tick();
        }
    }
}
