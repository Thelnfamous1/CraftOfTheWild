package com.Thelnfamous1.craft_of_the_wild.entity.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.BiPredicate;

/**
 * Subclass of {@link AnimatableMeleeAttack} that does the following:
 * <ul>
 *      <li> Sets the {@link MemoryModuleType#ATTACK_COOLING_DOWN} memory after attacking.
 *      <li> Allows adjusting the BiPredicate used for detecting if the target is within melee attack range.
 * </ul>
 */
public class COTWAnimatableMeleeAttack<E extends Mob> extends AnimatableMeleeAttack<E> {
    private BiPredicate<E, LivingEntity> isWithinMeleeAttackRange = Mob::isWithinMeleeAttackRange;
    public COTWAnimatableMeleeAttack(int delayTicks) {
        super(delayTicks);
    }

    public COTWAnimatableMeleeAttack<E> isWithinMeleeAttackRange(BiPredicate<E, LivingEntity> isWithinMeleeAttackRange){
        this.isWithinMeleeAttackRange = isWithinMeleeAttackRange;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = BrainUtils.getTargetOfEntity(entity);

        return entity.getSensing().hasLineOfSight(this.target) && this.isWithinMeleeAttackRange.test(entity, this.target);
    }

    /**
     * Same as {@link AnimatableMeleeAttack#doDelayedAction(Mob)} but with the attack cooldown set AFTER the attack
     */
    @Override
    protected void doDelayedAction(E entity) {
        if (this.target == null)
            return;

        if (!entity.getSensing().hasLineOfSight(this.target) || !this.isWithinMeleeAttackRange.test(entity, this.target))
            return;

        entity.doHurtTarget(this.target);
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
    }
}
