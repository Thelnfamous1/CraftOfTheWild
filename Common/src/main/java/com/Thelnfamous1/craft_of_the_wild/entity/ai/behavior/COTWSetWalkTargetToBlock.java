package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * Path setting behaviour for walking to/near a block position.
 * @param <E> The entity
 */
public class COTWSetWalkTargetToBlock<E extends PathfinderMob> extends ExtendedBehaviour<E> {
	protected BiPredicate<E, Pair<BlockPos, BlockState>> predicate = (entity, block) -> true;
	protected BiFunction<E, Pair<BlockPos, BlockState>, Float> speedMod = (owner, pos) -> 1f;
	protected BiFunction<E, Pair<BlockPos, BlockState>, Integer> closeEnoughDist = (entity, pos) -> 1;

	protected Pair<BlockPos, BlockState> target = null;

	private final List<Pair<MemoryModuleType<?>, MemoryStatus>> memoryRequirement;
	private final MemoryModuleType<BlockPos> blockMemory;

	public COTWSetWalkTargetToBlock(MemoryModuleType<BlockPos> blockMemory){
		this.memoryRequirement = Util.make(new ObjectArrayList<>(), list -> {
			list.add(Pair.of(blockMemory, MemoryStatus.VALUE_PRESENT));
			this.entryCondition.put(blockMemory, MemoryStatus.VALUE_PRESENT);
		});
		this.blockMemory = blockMemory;
	}

	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
		return Objects.requireNonNullElseGet(this.memoryRequirement, List::of);
	}

	/**
	 * Set the predicate to determine whether a given position/state should be the target path
	 * @param predicate The predicate
	 * @return this
	 */
	public COTWSetWalkTargetToBlock<E> predicate(final BiPredicate<E, Pair<BlockPos, BlockState>> predicate) {
		this.predicate = predicate;

		return this;
	}

	/**
	 * Set the movespeed modifier for the entity when moving to the target.
	 * @param speedModifier The movespeed modifier/multiplier
	 * @return this
	 */
	public COTWSetWalkTargetToBlock<E> speedMod(BiFunction<E, Pair<BlockPos, BlockState>, Float> speedModifier) {
		this.speedMod = speedModifier;

		return this;
	}

	/**
	 * Set the distance (in blocks) that is 'close enough' for the entity to be considered at the target position
	 * @param function The function
	 * @return this
	 */
	public COTWSetWalkTargetToBlock<E> closeEnoughWhen(final BiFunction<E, Pair<BlockPos, BlockState>, Integer> function) {
		this.closeEnoughDist = function;

		return this;
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
		BlockPos blockPos = BrainUtils.getMemory(entity, this.blockMemory);
		Pair<BlockPos, BlockState> position = Pair.of(blockPos, level.getBlockState(blockPos));
		if (this.predicate.test(entity, position)) {
			this.target = position;
		}

		return this.target != null;
	}

	@Override
	protected void start(E entity) {
		BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.target.getFirst(), this.speedMod.apply(entity, this.target), this.closeEnoughDist.apply(entity, this.target)));
		BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.target.getFirst()));
	}

	@Override
	protected void stop(E entity) {
		this.target = null;
	}
}