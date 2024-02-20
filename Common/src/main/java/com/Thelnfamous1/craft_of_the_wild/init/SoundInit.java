package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public class SoundInit {

    public static final RegistrationProvider<SoundEvent> SOUND_EVENTS = RegistrationProvider.get(Registries.SOUND_EVENT, Constants.MODID);

    public static final RegistryObject<SoundEvent> STONE_TALUS_BOSS_MUSIC = registerSoundEvent("stone_talus_boss_music");

    public static final RegistryObject<SoundEvent> STONE_TALUS_DEATH = registerSoundEvent("stone_talus_death");

    public static final RegistryObject<SoundEvent> STONE_TALUS_HEADBUTT = registerSoundEvent("stone_talus_headbutt");

    public static final RegistryObject<SoundEvent> STONE_TALUS_HURT = registerSoundEvent("stone_talus_hurt");

    public static final RegistryObject<SoundEvent> STONE_TALUS_POUND = registerSoundEvent("stone_talus_pound");

    public static final RegistryObject<SoundEvent> STONE_TALUS_SPAWN = registerSoundEvent("stone_talus_spawn");

    public static final RegistryObject<SoundEvent> STONE_TALUS_REGENERATE_ARMS = registerSoundEvent("stone_talus_regenerate_arms");

    public static final RegistryObject<SoundEvent> STONE_TALUS_THROW_ARMS = registerSoundEvent("stone_talus_throw_arms");

    public static final RegistryObject<SoundEvent> STONE_TALUS_WALK = registerSoundEvent("stone_talus_walk");

    public static final RegistryObject<SoundEvent> STONE_TALUS_STUN = registerSoundEvent("stone_talus_stun");

    public static final RegistryObject<SoundEvent> STONE_TALUS_PUNCH = registerSoundEvent("stone_talus_punch");

    public static final RegistryObject<SoundEvent> STONE_TALUS_SHAKE = registerSoundEvent("stone_talus_shake");

    private static RegistryObject<SoundEvent> registerSoundEvent(String path) {
        return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(COTWCommon.getResourceLocation(path)));
    }

    public static void loadClass(){

    }
}