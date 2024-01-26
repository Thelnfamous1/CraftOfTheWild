package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.sounds.SoundEvent;

public interface BossMusicPlayer {

    byte MUSIC_PLAY_ID = 67;
    byte MUSIC_STOP_ID = 68;

    SoundEvent getBossMusic();

    boolean canPlayBossMusic();

}
