package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWSleepAtHome<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT),
            Pair.of(MemoryModuleType.LAST_WOKEN, MemoryStatus.REGISTERED));
    public static final int COOLDOWN_AFTER_BEING_WOKEN = 100;
    private long nextOkStartTime;
    protected BiPredicate<E, GlobalPos> canStart = (e, home) -> {
        BlockState stateAtHome = e.level().getBlockState(home.pos());
        return home.pos().closerToCenterThan(e.position(), 2.0D)
                && stateAtHome.is(BlockTags.BEDS)
                && !stateAtHome.getValue(BedBlock.OCCUPIED);
    };
    protected BiPredicate<E, GlobalPos> canContinue = (e, home) -> {
        BlockPos homePos = home.pos();
        return e.getBrain().isActive(Activity.REST)
                && e.getY() > (double)homePos.getY() + 0.4
                && homePos.closerToCenterThan(e.position(), 1.14D);
    };

    public COTWSleepAtHome() {
        this.noTimeout();
    }

    public COTWSleepAtHome<E> canStart(BiPredicate<E, GlobalPos> canStart){
        this.canStart = canStart;
        return this;
    }

    public COTWSleepAtHome<E> canContinue(BiPredicate<E, GlobalPos> canContinue){
        this.canContinue = canContinue;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        if (entity.isPassenger()) {
            return false;
        } else {
            GlobalPos home = BrainUtils.getMemory(entity, MemoryModuleType.HOME);
            if (level.dimension() != home.dimension()) {
                return false;
            } else {
                Optional<Long> lastWoken = COTWUtil.getOptionalMemory(entity, MemoryModuleType.LAST_WOKEN);
                if (lastWoken.isPresent()) {
                    long ticksSinceLastWoken = level.getGameTime() - lastWoken.get();
                    if (ticksSinceLastWoken > 0L && ticksSinceLastWoken < COOLDOWN_AFTER_BEING_WOKEN) {
                        return false;
                    }
                }

                return this.canStart.test(entity, home);
            }
        }
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        Optional<GlobalPos> home = COTWUtil.getOptionalMemory(entity, MemoryModuleType.HOME);
        return home.filter(globalPos -> this.canContinue.test(entity, globalPos)).isPresent();
    }

    @Override
    protected void start(E entity) {
        if (entity.level().getGameTime() > this.nextOkStartTime) {
            if (BrainUtils.hasMemory(entity, MemoryModuleType.DOORS_TO_CLOSE)) {
                Set<GlobalPos> doorsToClose = BrainUtils.getMemory(entity, MemoryModuleType.DOORS_TO_CLOSE);
                Optional<List<LivingEntity>> nvle;
                if (BrainUtils.hasMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)) {
                    nvle = COTWUtil.getOptionalMemory(entity, MemoryModuleType.NEAREST_LIVING_ENTITIES);
                } else {
                    nvle = Optional.empty();
                }

                COTWInteractWithDoor.closeDoorsThatIHaveOpenedOrPassedThrough((ServerLevel) entity.level(), entity, null, null, doorsToClose, nvle);
            }

            entity.startSleeping(BrainUtils.getMemory(entity, MemoryModuleType.HOME).pos());
        }
    }

    @Override
    protected void stop(E entity) {
        if (entity.isSleeping()) {
            entity.stopSleeping();
            this.nextOkStartTime = entity.level().getGameTime() + 40L;
        }
    }
}