package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.NearestBurrowSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWAttackTargetSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.COTWNearbyPlayersSensor;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.SleepSensor;
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
    public static final RegistryObject<SensorType<COTWNearbyPlayersSensor<?>>> NEARBY_PLAYERS = register("nearby_players", COTWNearbyPlayersSensor::new);
    public static final RegistryObject<SensorType<NearestBurrowSensor<?>>> NEAREST_BURROW = register("nearest_burrow", NearestBurrowSensor::new);
    private static <U extends Sensor<?>> RegistryObject<SensorType<U>> register(String path, Supplier<U> supplier){
        return SENSOR_TYPES.register(path, () -> new SensorType<>(supplier));
    }

    public static void loadClass() {

    }
}
