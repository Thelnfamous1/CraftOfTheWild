package com.Thelnfamous1.craft_of_the_wild.entity.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.BiFunction;

/**
 * Subclass of {@link AnimatableRangedAttack} that allows for modifying the distance comparison function between attacker and target.
 * This is helpful for adjusting the perceived distance calculation from center-to-center to hitbox-to-hitbox if needed.
 */
public class COTWAnimatableRangedAttack<E extends LivingEntity & RangedAttackMob> extends AnimatableRangedAttack<E> {
    protected BiFunction<E, LivingEntity, Double> getPerceivedTargetDistanceSquared = Entity::distanceToSqr;

    public COTWAnimatableRangedAttack(int delayTicks) {
        super(delayTicks);
    }

    public COTWAnimatableRangedAttack<E> getPerceivedTargetDistanceSquared(BiFunction<E, LivingEntity, Double> getPerceivedTargetDistanceSquared){
        this.getPerceivedTargetDistanceSquared = getPerceivedTargetDistanceSquared;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = BrainUtils.getTargetOfEntity(entity);

        return BrainUtils.canSee(entity, this.target) && this.getPerceivedTargetDistanceSquared.apply(entity, this.target) <= this.attackRadius;
    }

    @Override
    protected void doDelayedAction(E entity) {
        if (this.target == null)
            return;

        if (!BrainUtils.canSee(entity, this.target) || this.getPerceivedTargetDistanceSquared.apply(entity, this.target) > this.attackRadius)
            return;

        entity.performRangedAttack(this.target, 1);
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
    }

}
