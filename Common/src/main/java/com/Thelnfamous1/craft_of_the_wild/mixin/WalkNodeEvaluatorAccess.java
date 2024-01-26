package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WalkNodeEvaluator.class)
public interface WalkNodeEvaluatorAccess {

    @Invoker
    static boolean callDoesBlockHavePartialCollision(BlockPathTypes $$0){
        throw new AssertionError();
    }

    @Invoker
    double callGetMobJumpHeight();

    @Invoker
    Node callGetNodeAndUpdateCostToMax(int pX, int pY, int pZ, BlockPathTypes pType, float pCostMalus);

    @Invoker
    Node callGetBlockedNode(int pX, int pY, int pZ);

    @Invoker
    boolean callHasCollisions(AABB pBoundingBox);

    @Invoker
    boolean callCanReachWithoutCollision(Node pNode);
}
