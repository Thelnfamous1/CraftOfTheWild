package com.Thelnfamous1.craft_of_the_wild.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class COTWLookAndFollowTradingPlayerSink<T extends LivingEntity & Merchant> extends ExtendedBehaviour<T> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
    private float speedModifier = 1.0F;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    public COTWLookAndFollowTradingPlayerSink() {
        this.noTimeout();
    }

    public COTWLookAndFollowTradingPlayerSink<T> speedModifier(float speedModifier){
        this.speedModifier = speedModifier;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T entity) {
        Player player = entity.getTradingPlayer();
        return entity.isAlive() && player != null && !entity.isInWater() && !entity.hurtMarked && entity.distanceToSqr(player) <= 16.0 && player.containerMenu != null;
    }

    @Override
    protected boolean shouldKeepRunning(T entity) {
        return this.checkExtraStartConditions((ServerLevel) entity.level(), entity);
    }

    @Override
    protected void start(T entity) {
        this.followPlayer(entity);
    }

    @Override
    protected void stop(T entity) {
        BrainUtils.clearMemories(entity, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void tick(T entity) {
        this.followPlayer(entity);
    }

    protected void followPlayer(T entity) {
        Player player = entity.getTradingPlayer();
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(player, false), this.speedModifier, 2));
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(player, true));
    }
}