package com.Thelnfamous1.craft_of_the_wild.entity.ai.navigation;

import com.Thelnfamous1.craft_of_the_wild.mixin.WalkNodeEvaluatorAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Credit: <a href="https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/server/ai/MMWalkNodeProcessor.java">Mowzie's Mobs</a>
 */
public class COTWWalkNodeProcessor extends WalkNodeEvaluator {

    @Override
    public Node getStart() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        AABB boundingBox = this.mob.getBoundingBox();
        int y = (int) boundingBox.minY;
        BlockState blockState = this.level.getBlockState(mutableBlockPos.set(this.mob.getX(), y, this.mob.getZ()));
        BlockPos blockPos;
        if (!this.mob.canStandOnFluid(blockState.getFluidState())) {
            if (this.canFloat() && this.mob.isInWater()) {
                while(true) {
                    if (!blockState.is(Blocks.WATER) && blockState.getFluidState() != Fluids.WATER.getSource(false)) {
                        --y;
                        break;
                    }

                    ++y;
                    blockState = this.level.getBlockState(mutableBlockPos.set(this.mob.getX(), y, this.mob.getZ()));
                }
            } else if (this.mob.onGround()) {
                y = Mth.floor(boundingBox.minY + 0.5);
            } else {
                //noinspection StatementWithEmptyBody
                for(blockPos = this.mob.blockPosition();
                    (this.level.getBlockState(blockPos).isAir()
                            || this.level.getBlockState(blockPos).isPathfindable(this.level, blockPos, PathComputationType.LAND))
                            && blockPos.getY() > this.mob.level().getMinBuildHeight();
                    blockPos = blockPos.below()) {
                }

                y = blockPos.above().getY();
            }
        } else {
            while(true) {
                if (!this.mob.canStandOnFluid(blockState.getFluidState())) {
                    --y;
                    break;
                }

                ++y;
                blockState = this.level.getBlockState(mutableBlockPos.set(this.mob.getX(), y, this.mob.getZ()));
            }
        }

        // Mowzie's Mobs: "account for node size"
        float radius = this.mob.getBbWidth() * 0.5F;
        int x = Mth.floor(this.mob.getX() - radius);
        int z = Mth.floor(this.mob.getZ() - radius);
        if (!this.canStartAt(mutableBlockPos.set(x, y, z))) {
            if (this.canStartAt(mutableBlockPos.set(boundingBox.minX - radius, y, boundingBox.minZ - radius))
                    || this.canStartAt(mutableBlockPos.set(boundingBox.minX - radius, y, boundingBox.maxZ - radius))
                    || this.canStartAt(mutableBlockPos.set(boundingBox.maxX - radius, y, boundingBox.minZ - radius))
                    || this.canStartAt(mutableBlockPos.set(boundingBox.maxX - radius, y, boundingBox.maxZ - radius))) {
                return this.getStartNode(mutableBlockPos);
            }
        }

        return this.getStartNode(BlockPos.containing(x, y, z));
    }

    @Nullable
    protected Node findAcceptedNode(int pX, int pY, int pZ, int pStep, double pFloor, Direction pDirection, BlockPathTypes pPathType) {
        Node result = null;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        double floorLevel = this.getFloorLevel(mutableBlockPos.set(pX, pY, pZ));
        if (floorLevel - pFloor > ((WalkNodeEvaluatorAccess)this).callGetMobJumpHeight()) {
            return null;
        } else {
            BlockPathTypes cachedBlockType = this.getCachedBlockType(this.mob, pX, pY, pZ);
            float pathfindingMalus = this.mob.getPathfindingMalus(cachedBlockType);
            double radius = (double)this.mob.getBbWidth() / 2.0D;
            if (pathfindingMalus >= 0.0F) {
                result = ((WalkNodeEvaluatorAccess)this).callGetNodeAndUpdateCostToMax(pX, pY, pZ, cachedBlockType, pathfindingMalus);
            }

            if (WalkNodeEvaluatorAccess.callDoesBlockHavePartialCollision(pPathType) && result != null && result.costMalus >= 0.0F && !((WalkNodeEvaluatorAccess)this).callCanReachWithoutCollision(result)) {
                result = null;
            }

            if (cachedBlockType != BlockPathTypes.WALKABLE && (!this.isAmphibious() || cachedBlockType != BlockPathTypes.WATER)) {
                if ((result == null || result.costMalus < 0.0F) && pStep > 0 && (cachedBlockType != BlockPathTypes.FENCE || this.canWalkOverFences()) && cachedBlockType != BlockPathTypes.UNPASSABLE_RAIL && cachedBlockType != BlockPathTypes.TRAPDOOR && cachedBlockType != BlockPathTypes.POWDER_SNOW) {
                    result = this.findAcceptedNode(pX, pY + 1, pZ, pStep - 1, pFloor, pDirection, pPathType);
                    if (result != null && (result.type == BlockPathTypes.OPEN || result.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0F) {
                        double d2 = (double)(pX - pDirection.getStepX()) + 0.5D;
                        double d3 = (double)(pZ - pDirection.getStepZ()) + 0.5D;
                        AABB aabb = new AABB(
                                d2 - radius,
                                this.getFloorLevel(mutableBlockPos.set(d2, pY + 1, d3)) + 0.001D,
                                d3 - radius,
                                d2 + radius,
                                (double)this.mob.getBbHeight() + this.getFloorLevel(mutableBlockPos.set(result.x, result.y, (double)result.z)) - 0.002D,
                                d3 + radius);
                        if (((WalkNodeEvaluatorAccess)this).callHasCollisions(aabb)) {
                            result = null;
                        }
                    }
                }

                if (!this.isAmphibious() && cachedBlockType == BlockPathTypes.WATER && !this.canFloat()) {
                    if (this.getCachedBlockType(this.mob, pX, pY - 1, pZ) != BlockPathTypes.WATER) {
                        return result;
                    }

                    while(pY > this.mob.level().getMinBuildHeight()) {
                        --pY;
                        cachedBlockType = this.getCachedBlockType(this.mob, pX, pY, pZ);
                        if (cachedBlockType != BlockPathTypes.WATER) {
                            return result;
                        }

                        result = ((WalkNodeEvaluatorAccess)this).callGetNodeAndUpdateCostToMax(pX, pY, pZ, cachedBlockType, this.mob.getPathfindingMalus(cachedBlockType));
                    }
                }

                if (cachedBlockType == BlockPathTypes.OPEN) {
                    // Mowzie's Mobs: "account for node size"
                    AABB collision = new AABB(
                            pX - radius + this.entityWidth * 0.5D, pY + 0.001D, pZ - radius + this.entityDepth * 0.5D,
                            pX + radius + this.entityWidth * 0.5D, pY + this.mob.getBbHeight(), pZ + radius + this.entityDepth * 0.5D
                    );
                    if (((WalkNodeEvaluatorAccess)this).callHasCollisions(collision)) {
                        return null;
                    }
                    if (this.mob.getBbWidth() >= 1.0F) {
                        BlockPathTypes down = this.getCachedBlockType(this.mob, pX, pY - 1, pZ);
                        if (down == BlockPathTypes.BLOCKED) {
                            result = this.getNode(pX, pY, pZ);
                            result.type = BlockPathTypes.WALKABLE;
                            result.costMalus = Math.max(result.costMalus, pathfindingMalus);
                            return result;
                        }
                    }


                    int fallDistance = 0;
                    int i = pY;

                    while(cachedBlockType == BlockPathTypes.OPEN) {
                        --pY;
                        if (pY < this.mob.level().getMinBuildHeight()) {
                            return ((WalkNodeEvaluatorAccess)this).callGetBlockedNode(pX, i, pZ);
                        }

                        if (fallDistance++ >= this.mob.getMaxFallDistance()) {
                            return ((WalkNodeEvaluatorAccess)this).callGetBlockedNode(pX, pY, pZ);
                        }

                        cachedBlockType = this.getCachedBlockType(this.mob, pX, pY, pZ);
                        pathfindingMalus = this.mob.getPathfindingMalus(cachedBlockType);
                        if (cachedBlockType != BlockPathTypes.OPEN && pathfindingMalus >= 0.0F) {
                            result = ((WalkNodeEvaluatorAccess)this).callGetNodeAndUpdateCostToMax(pX, pY, pZ, cachedBlockType, pathfindingMalus);
                            break;
                        }

                        if (pathfindingMalus < 0.0F) {
                            return ((WalkNodeEvaluatorAccess)this).callGetBlockedNode(pX, pY, pZ);
                        }
                    }
                }

                if (WalkNodeEvaluatorAccess.callDoesBlockHavePartialCollision(cachedBlockType) && result == null) {
                    result = this.getNode(pX, pY, pZ);
                    result.closed = true;
                    result.type = cachedBlockType;
                    result.costMalus = cachedBlockType.getMalus();
                }

                return result;
            } else {
                return result;
            }
        }
    }

    public BlockPathTypes getBlockPathTypeWithCustomEntitySize(BlockGetter pLevel, int pX, int pY, int pZ, Mob pMob, int entityWidth, int entityHeight, int entityDepth) {
        EnumSet<BlockPathTypes> blockPathTypes = EnumSet.noneOf(BlockPathTypes.class);
        BlockPathTypes blockPathType = BlockPathTypes.BLOCKED;
        blockPathType = this.getBlockPathTypesWithCustomEntitySize(pLevel, pX, pY, pZ, blockPathTypes, blockPathType, pMob.blockPosition(), entityWidth, entityHeight, entityDepth);
        if (blockPathTypes.contains(BlockPathTypes.FENCE)) {
            return BlockPathTypes.FENCE;
        } else if (blockPathTypes.contains(BlockPathTypes.UNPASSABLE_RAIL)) {
            return BlockPathTypes.UNPASSABLE_RAIL;
        } else {
            BlockPathTypes blockpathtypes1 = BlockPathTypes.BLOCKED;

            for(BlockPathTypes blockpathtypes2 : blockPathTypes) {
                if (pMob.getPathfindingMalus(blockpathtypes2) < 0.0F) {
                    return blockpathtypes2;
                }

                if (pMob.getPathfindingMalus(blockpathtypes2) >= pMob.getPathfindingMalus(blockpathtypes1)) {
                    blockpathtypes1 = blockpathtypes2;
                }
            }

            return blockPathType == BlockPathTypes.OPEN && pMob.getPathfindingMalus(blockpathtypes1) == 0.0F && this.entityWidth <= 1 ? BlockPathTypes.OPEN : blockpathtypes1;
        }
    }

    private BlockPathTypes getBlockPathTypesWithCustomEntitySize(BlockGetter pLevel, int pXOffset, int pYOffset, int pZOffset, EnumSet<BlockPathTypes> pOutput, BlockPathTypes resultType, BlockPos pPos, int entityWidth, int entityHeight, int entityDepth) {
        for(int xStep = 0; xStep < entityWidth; ++xStep) {
            for(int yStep = 0; yStep < entityHeight; ++yStep) {
                for(int zStep = 0; zStep < entityDepth; ++zStep) {
                    int x = xStep + pXOffset;
                    int y = yStep + pYOffset;
                    int z = zStep + pZOffset;
                    BlockPathTypes currentType = this.getBlockPathType(pLevel, x, y, z);
                    currentType = this.evaluateBlockPathType(pLevel, pPos, currentType);
                    if (xStep == 0 && yStep == 0 && zStep == 0) {
                        resultType = currentType;
                    }

                    pOutput.add(currentType);
                }
            }
        }

        return resultType;
    }
}