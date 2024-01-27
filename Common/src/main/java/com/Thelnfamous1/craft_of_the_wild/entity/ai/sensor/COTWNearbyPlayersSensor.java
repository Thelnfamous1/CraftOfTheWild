package com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor;

import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.init.SensorInit;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.Comparator;
import java.util.List;

public class COTWNearbyPlayersSensor<E extends LivingEntity> extends NearbyPlayersSensor<E> {

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return SensorInit.NEARBY_PLAYERS.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        SquareRadius radius = this.radius;

        if (radius == null) {
            double dist = entity.getAttributeValue(Attributes.FOLLOW_RANGE);

            radius = new SquareRadius(dist, dist);
        }
        // Utilizing yRadius as well would require a subclass/patch of TargetingConditions
        // The subclass/patch would need to separate xz dist check from y dist check
        // Such a subclass/patch is outside the scope of this mod
        double targetingDistance = radius.xzRadius();

        List<Player> players = EntityRetrievalUtil.getPlayers(level, radius.inflateAABB(entity.getBoundingBox()), player -> predicate().test(player, entity));

        players.sort(Comparator.comparingDouble(entity::distanceToSqr));

        List<Player> targetablePlayers = new ObjectArrayList<>(players);

        targetablePlayers.removeIf(pl -> !COTWSharedAi.isEntityTargetable(entity, pl, targetingDistance));

        List<Player> attackablePlayers = new ObjectArrayList<>(targetablePlayers);

        attackablePlayers.removeIf(pl -> !COTWSharedAi.isEntityAttackable(entity, pl, targetingDistance));

        BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_PLAYERS, players);
        BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_VISIBLE_PLAYER, targetablePlayers.isEmpty() ? null : targetablePlayers.get(0));
        BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, attackablePlayers.isEmpty() ? null : attackablePlayers.get(0));
    }
}
