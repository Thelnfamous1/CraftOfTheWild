package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWAttackTargetSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.SleepSensor;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.function.Supplier;

public class SensorInit {
    public static final RegistrationProvider<SensorType<?>> SENSOR_TYPES = RegistrationProvider.get(Registries.SENSOR_TYPE, Constants.MODID);

    public static RegistryObject<SensorType<COTWAttackTargetSensor<?>>> ATTACK_TARGET = register("attack_target", COTWAttackTargetSensor::new);

    public static RegistryObject<SensorType<SleepSensor<?>>> SLEEP = register("sleep", SleepSensor::new);
    private static <U extends Sensor<?>> RegistryObject<SensorType<U>> register(String path, Supplier<U> supplier){
        return SENSOR_TYPES.register(path, () -> new SensorType<>(supplier));
    }

    public static void loadClass() {

    }
}
