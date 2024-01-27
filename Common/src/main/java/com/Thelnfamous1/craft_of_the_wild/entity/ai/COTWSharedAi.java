package com.Thelnfamous1.craft_of_the_wild.entity.ai;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.HashMap;
import java.util.Map;

public class COTWSharedAi {
    private static final Map<Double, TargetingConditions> TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = new HashMap<>();
    private static final Map<Double, TargetingConditions> TARGET_CONDITIONS = new HashMap<>();
    private static final Map<Double, TargetingConditions> ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = new HashMap<>();
    private static final Map<Double, TargetingConditions> ATTACK_TARGET_CONDITIONS = new HashMap<>();
    private static final Map<Double, TargetingConditions> ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT = new HashMap<>();
    private static final Map<Double, TargetingConditions> ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = new HashMap<>();

    public static ExtendedBehaviour<Mob> createVanillaStyleLookAtTarget() {
        return new LookAtTarget<>()
                .stopIf(mob -> COTWUtil.getOptionalMemory(mob, MemoryModuleType.LOOK_TARGET)
                        .filter(pt -> pt.isVisibleBy(mob))
                        .isEmpty())
                .whenStopping(talus -> BrainUtils.clearMemory(talus, MemoryModuleType.LOOK_TARGET))
                .runFor(talus -> talus.getRandom().nextIntBetweenInclusive(45, 90));
    }

    public static boolean isEntityTargetable(LivingEntity targeter, LivingEntity target, double distance) {
        return targeter.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, target) ?
                TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.computeIfAbsent(distance, k -> TargetingConditions.forNonCombat().range(distance)).test(targeter, target) :
                TARGET_CONDITIONS.computeIfAbsent(distance, k -> TargetingConditions.forNonCombat().range(distance).ignoreInvisibilityTesting()).test(targeter, target);
    }

    public static boolean isEntityAttackable(LivingEntity attacker, LivingEntity target, double distance) {
        return attacker.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, target) ?
                ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.computeIfAbsent(distance, k -> TargetingConditions.forCombat().range(distance)).test(attacker, target) :
                ATTACK_TARGET_CONDITIONS.computeIfAbsent(distance, k -> TargetingConditions.forCombat().range(distance).ignoreInvisibilityTesting()).test(attacker, target);
    }

    public static boolean isEntityAttackableIgnoringLineOfSight(LivingEntity attacker, LivingEntity target, double distance) {
        return attacker.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, target) ?
                ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.computeIfAbsent(distance, k -> TargetingConditions.forCombat().range(distance).ignoreLineOfSight()).test(attacker, target) :
                ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.computeIfAbsent(distance, k -> TargetingConditions.forCombat().range(distance).ignoreLineOfSight().ignoreInvisibilityTesting()).test(attacker, target);
    }
}
