package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface CustomMusicPlayer {

    byte MUSIC_PLAY_ID = 67;
    byte MUSIC_STOP_ID = 69;

    static <T extends LivingEntity & CustomMusicPlayer> void handleCustomMusicEvent(T musicPlayer, byte eventId) {
        if (eventId == CustomMusicPlayer.MUSIC_PLAY_ID) {
            COTWCommon.playCustomMusicFor(musicPlayer);
        } else if (eventId == CustomMusicPlayer.MUSIC_STOP_ID) {
            COTWCommon.stopCustomMusicFor(musicPlayer);
        }
    }

    SoundEvent getCustomMusic();

    boolean canPlayCustomMusic();

    boolean canCustomMusicBeHeardBy(Player player);
}
