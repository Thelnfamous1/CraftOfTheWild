package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.BiPredicate;

/**
 * Subclass of {@link AnimatableMeleeAttack} that allows adjusting the BiPredicate used for detecting if the target is within attack range.
 * This is helpful for using hitbox-to-hitbox instead of center-to-center distance checks if needed.
 */

public class COTWSetWalkTargetToAttackTarget<E extends Mob> extends SetWalkTargetToAttackTarget<E> {

    private BiPredicate<E, LivingEntity> isWithinAttackRange = (attacker, target) -> BehaviorUtils.isWithinAttackRange(attacker, target, 1);

    public COTWSetWalkTargetToAttackTarget<E> isWithinAttackRange(BiPredicate<E, LivingEntity> isWithinAttackRange){
        this.isWithinAttackRange = isWithinAttackRange;
        return this;
    }

    @Override
    protected void start(E entity) {
        Brain<?> brain = entity.getBrain();
        LivingEntity target = BrainUtils.getTargetOfEntity(entity);

        if (entity.getSensing().hasLineOfSight(target) && this.isWithinAttackRange.test(entity, target)) {
            BrainUtils.clearMemory(brain, MemoryModuleType.WALK_TARGET);
        }
        else {
            BrainUtils.setMemory(brain, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
            BrainUtils.setMemory(brain, MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(target, false), this.speedMod.apply(entity, target), this.closeEnoughWhen.apply(entity, target)));
        }
    }
}
