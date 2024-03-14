package com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.init.MemoryModuleInit;
import com.Thelnfamous1.craft_of_the_wild.init.SensorInit;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NearestBurrowSensor<E extends Mob> extends PredicateSensor<E, E> {

    public static final ObjectArrayList<MemoryModuleType<?>> MEMORY_REQUIREMENTS = ObjectArrayList.of(MemoryModuleInit.NEAREST_BURROW.get());
    protected SquareRadius radius = new SquareRadius(8, 4);

    private final Object2LongOpenHashMap<BlockPos> burrowsMap = new Object2LongOpenHashMap<>(5);
    private int tries = 0;

    public NearestBurrowSensor() {
    }

    public NearestBurrowSensor<E> setRadius(double radius) {
        return setRadius(radius, radius);
    }

    public NearestBurrowSensor<E> setRadius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);

        return this;
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return SensorInit.NEAREST_BURROW.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        if (!this.predicate().test(entity, entity))
            return;

        this.tries = 0;
        long nodeExpiryTime = level.getGameTime() + level.getRandom().nextInt(20);
        Predicate<BlockPos> burrowCollectorPredicate = pos -> {
            if (this.burrowsMap.containsKey(pos))
                return false;

            if (++this.tries >= 5)
                return false;

            if (!this.canBurrowInto(level, entity, pos)) return false;

            this.burrowsMap.put(pos, nodeExpiryTime + 40);
            return true;
        };


        int xzRadius = (int) this.radius.xzRadius();
        Set<BlockPos> burrowLocations = BlockPos.withinManhattanStream(entity.blockPosition(), xzRadius, (int) this.radius.yRadius(), xzRadius)
                .filter(burrowCollectorPredicate)
                .map(BlockPos::immutable)
                .collect(Collectors.toSet());
        Path pathToBurrow;
        try{
            pathToBurrow = entity.getNavigation().createPath(burrowLocations, xzRadius);
        } catch(IllegalStateException exception){
            Constants.LOG.error("Unable to create path for burrow location set with duplicates: {}", burrowLocations);
            throw exception;
        }
        if (pathToBurrow != null && pathToBurrow.canReach()) {
            BlockPos targetPos = pathToBurrow.getTarget();
            if(!this.canBurrowInto(level, entity, targetPos)){
                COTWCommon.debug(Constants.DEBUG_NEAREST_BURROW_SENSOR, "{} cannot burrow into target position {}", entity, targetPos);
                BrainUtils.clearMemory(entity, MemoryModuleInit.NEAREST_BURROW.get());
            } else{
                COTWCommon.debug(Constants.DEBUG_NEAREST_BURROW_SENSOR, "Set {} memory to {} for {}", MemoryModuleInit.NEAREST_BURROW.getId(), targetPos, entity);
                BrainUtils.setMemory(entity, MemoryModuleInit.NEAREST_BURROW.get(), targetPos);
            }
        }
        else if (this.tries < 5) {
            this.burrowsMap.object2LongEntrySet().removeIf(pos -> pos.getLongValue() < nodeExpiryTime);
        }
    }

    private boolean canBurrowInto(ServerLevel level, E entity, BlockPos pos) {
        AABB entityBB = entity.getBoundingBox();
        return BlockPos.betweenClosedStream(AABB.ofSize(pos.getCenter(), entityBB.getXsize(), entityBB.getYsize(), entityBB.getZsize()).move(0, -entityBB.getYsize() * 0.5F, 0))
                .noneMatch(bp -> level.getBlockState(bp).canBeReplaced());
    }
}
