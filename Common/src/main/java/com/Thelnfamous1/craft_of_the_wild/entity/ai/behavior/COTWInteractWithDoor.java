package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWInteractWithDoor<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT),
            Pair.of(MemoryModuleType.DOORS_TO_CLOSE, MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.REGISTERED));
    private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 20;
    private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 3.0;
    private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2.0;
    @Nullable
    private Node currentNode;
    private int checkCooldown;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Path path = BrainUtils.getMemory(entity, MemoryModuleType.PATH);
        Optional<Set<GlobalPos>> doorsToClose = COTWUtil.getOptionalMemory(entity, MemoryModuleType.DOORS_TO_CLOSE);
        if (!path.notStarted() && !path.isDone()) {
            if (Objects.equals(this.currentNode, path.getNextNode())) {
                this.checkCooldown = COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE;
            } else if (checkCooldown-- > 0) {
                return false;
            }

            this.currentNode = path.getNextNode();
            Node previousNode = path.getPreviousNode();
            Node nextNode = path.getNextNode();
            BlockPos previousBlockPos = previousNode.asBlockPos();
            BlockState previousBlockState = level.getBlockState(previousBlockPos);
            if (previousBlockState.is(BlockTags.WOODEN_DOORS, (bsb) -> bsb.getBlock() instanceof DoorBlock)) {
                DoorBlock doorBlock = (DoorBlock)previousBlockState.getBlock();
                if (!doorBlock.isOpen(previousBlockState)) {
                    doorBlock.setOpen(entity, level, previousBlockState, previousBlockPos, true);
                }

                doorsToClose = rememberDoorToClose(entity.getBrain(), doorsToClose, level, previousBlockPos);
            }

            BlockPos nextBlockPos = nextNode.asBlockPos();
            BlockState nextBlockState = level.getBlockState(nextBlockPos);
            if (nextBlockState.is(BlockTags.WOODEN_DOORS, (bsb) -> bsb.getBlock() instanceof DoorBlock)) {
                DoorBlock doorBlock = (DoorBlock)nextBlockState.getBlock();
                if (!doorBlock.isOpen(nextBlockState)) {
                    doorBlock.setOpen(entity, level, nextBlockState, nextBlockPos, true);
                    doorsToClose = rememberDoorToClose(entity.getBrain(), doorsToClose, level, nextBlockPos);
                }
            }

            doorsToClose.ifPresent((dtc) -> closeDoorsThatIHaveOpenedOrPassedThrough(level, entity, previousNode, nextNode, dtc, COTWUtil.getOptionalMemory(entity, MemoryModuleType.NEAREST_LIVING_ENTITIES)));
            return true;
        } else {
            return false;
        }
    }

    public static void closeDoorsThatIHaveOpenedOrPassedThrough(ServerLevel level, LivingEntity entity, @Nullable Node previousNode, @Nullable Node nextNode, Set<GlobalPos> doorsToClose, Optional<List<LivingEntity>> nearbyLivingEntities) {
        Iterator<GlobalPos> dtcIterator = doorsToClose.iterator();

        while(true) {
            GlobalPos doorToClose;
            BlockPos dtcBlockPos;
            do {
                do {
                    if (!dtcIterator.hasNext()) {
                        return;
                    }

                    doorToClose = dtcIterator.next();
                    dtcBlockPos = doorToClose.pos();
                } while(previousNode != null && previousNode.asBlockPos().equals(dtcBlockPos));
            } while(nextNode != null && nextNode.asBlockPos().equals(dtcBlockPos));

            if (isDoorTooFarAway(level, entity, doorToClose)) {
                dtcIterator.remove();
            } else {
                BlockState dtcBlockState = level.getBlockState(dtcBlockPos);
                if (!dtcBlockState.is(BlockTags.WOODEN_DOORS, ($$0x) -> $$0x.getBlock() instanceof DoorBlock)) {
                    dtcIterator.remove();
                } else {
                    DoorBlock doorBlock = (DoorBlock)dtcBlockState.getBlock();
                    if (!doorBlock.isOpen(dtcBlockState)) {
                        dtcIterator.remove();
                    } else if (areOtherMobsComingThroughDoor(entity, dtcBlockPos, nearbyLivingEntities)) {
                        dtcIterator.remove();
                    } else {
                        doorBlock.setOpen(entity, level, dtcBlockState, dtcBlockPos, false);
                        dtcIterator.remove();
                    }
                }
            }
        }
    }

    private static boolean areOtherMobsComingThroughDoor(LivingEntity entity, BlockPos blockPos, Optional<List<LivingEntity>> nearbyLivingEntities) {
        return nearbyLivingEntities.map(livingEntities -> (livingEntities)
                        .stream()
                        .filter((le) -> le.getType() == entity.getType())
                        .filter((le) -> blockPos.closerToCenterThan(le.position(), MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS))
                        .anyMatch((le) -> isMobComingThroughDoor(le.getBrain(), blockPos)))
                .orElse(false);
    }

    private static boolean isMobComingThroughDoor(Brain<?> brain, BlockPos blockPos) {
        if (!brain.hasMemoryValue(MemoryModuleType.PATH)) {
            return false;
        } else {
            Path path = BrainUtils.getMemory(brain, MemoryModuleType.PATH);
            if (path.isDone()) {
                return false;
            } else {
                Node previousNode = path.getPreviousNode();
                if (previousNode == null) {
                    return false;
                } else {
                    Node nextNode = path.getNextNode();
                    return blockPos.equals(previousNode.asBlockPos()) || blockPos.equals(nextNode.asBlockPos());
                }
            }
        }
    }

    private static boolean isDoorTooFarAway(ServerLevel level, LivingEntity entity, GlobalPos globalPos) {
        return globalPos.dimension() != level.dimension() || !globalPos.pos().closerToCenterThan(entity.position(), SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN);
    }

    private static Optional<Set<GlobalPos>> rememberDoorToClose(Brain<?> brain, Optional<Set<GlobalPos>> doorsToClose, ServerLevel level, BlockPos blockPos) {
        GlobalPos globalPos = GlobalPos.of(level.dimension(), blockPos);
        return Optional.of(doorsToClose.map((dtc) -> {
            dtc.add(globalPos);
            return dtc;
        }).orElseGet(() -> {
            Set<GlobalPos> dtc = Sets.newHashSet(globalPos);
            BrainUtils.setMemory(brain, MemoryModuleType.DOORS_TO_CLOSE, dtc);
            return dtc;
        }));
    }
}