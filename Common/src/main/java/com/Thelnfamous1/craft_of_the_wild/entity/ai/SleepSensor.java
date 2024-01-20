package com.Thelnfamous1.craft_of_the_wild.entity.ai;

import com.Thelnfamous1.craft_of_the_wild.init.MemoryModuleInit;
import com.Thelnfamous1.craft_of_the_wild.init.SensorInit;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class SleepSensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORY_REQUIREMENTS = ObjectArrayList.of(MemoryModuleInit.IS_SLEEPING.get());

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return SensorInit.SLEEP.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        if(entity.hasPose(Pose.SLEEPING)){
            BrainUtils.setMemory(entity, MemoryModuleInit.IS_SLEEPING.get(), true);
        } else{
            BrainUtils.clearMemory(entity, MemoryModuleInit.IS_SLEEPING.get());
        }
    }
}
