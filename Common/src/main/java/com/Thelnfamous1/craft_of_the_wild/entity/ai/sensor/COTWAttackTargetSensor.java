package com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor;

import com.Thelnfamous1.craft_of_the_wild.init.SensorInit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.EntityFilteringSensor;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class COTWAttackTargetSensor<E extends LivingEntity> extends EntityFilteringSensor<LivingEntity, E> {
	private BiPredicate<LivingEntity, E> attackablePredicate = (target, entity) -> isEntityAttackable(entity, target);

	@Override
	protected MemoryModuleType<LivingEntity> getMemory() {
		return MemoryModuleType.NEAREST_ATTACKABLE;
	}

	@Override
	public PredicateSensor<LivingEntity, E> setPredicate(BiPredicate<LivingEntity, E> predicate) {
		this.attackablePredicate = predicate;
		return this;
	}

	@Override
	protected BiPredicate<LivingEntity, E> predicate() {
		return this.attackablePredicate;
	}

	@Nullable
	@Override
	protected LivingEntity findMatches(E entity, NearestVisibleLivingEntities matcher) {
		return matcher.findClosest(target -> predicate().test(target, entity)).orElse(null);
	}

	@Override
	public SensorType<? extends ExtendedSensor<?>> type() {
		return SensorInit.ATTACK_TARGET.get();
	}
}