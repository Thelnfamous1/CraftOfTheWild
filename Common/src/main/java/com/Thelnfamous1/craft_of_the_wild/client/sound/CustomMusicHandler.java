package com.Thelnfamous1.craft_of_the_wild.client.sound;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.CustomMusicPlayer;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class CustomMusicHandler<T extends Entity & CustomMusicPlayer> {
    private static final int LOOP_SOUND_CROSS_FADE_TIME = 40;
    private final LocalPlayer player;
    private final SoundManager soundManager;
    private final Object2ObjectArrayMap<CustomMusicPlayer.CustomMusicData, CustomMusicSoundInstance<?>> customMusicInstances = new Object2ObjectArrayMap<>();
    @Nullable
    private T previousMusicPlayer;
    @Nullable
    private T currentMusicPlayer;
    @Nullable
    private CustomMusicPlayer.CustomMusicData previousMusic;
    @Nullable
    private CustomMusicPlayer.CustomMusicData currentMusic;

    public CustomMusicHandler(LocalPlayer player, SoundManager soundManager) {
        this.player = player;
        this.soundManager = soundManager;
    }

    public void playCustomMusicFor(T musicPlayer){
        if((this.currentMusicPlayer != musicPlayer || this.currentMusic != musicPlayer.getCustomMusic()) && musicPlayer.isAlive() && musicPlayer.canCustomMusicBeHeardBy(this.player)){
            COTWCommon.debug(Constants.DEBUG_CUSTOM_MUSIC, "Playing custom music for {}", musicPlayer);
            this.currentMusicPlayer = musicPlayer;
            this.currentMusic = musicPlayer.getCustomMusic();
        }
    }

    public void stopCustomMusicFor(T musicPlayer){
        if(this.currentMusicPlayer == musicPlayer){
            COTWCommon.debug(Constants.DEBUG_CUSTOM_MUSIC, "Stopping custom music for {}", musicPlayer);
            this.currentMusicPlayer = null;
            this.currentMusic = null;
        }
    }

    public void tick() {
        if (this.currentMusicPlayer != null) {
            if (this.currentMusicPlayer.isAlive()) {
                if(!this.currentMusicPlayer.canCustomMusicBeHeardBy(this.player)){
                    COTWCommon.debug(Constants.DEBUG_CUSTOM_MUSIC, "{} cannot hear custom music for {}", this.player, this.currentMusicPlayer);
                    this.stopCustomMusicFor(this.currentMusicPlayer);
                }
            } else{
                this.stopCustomMusicFor(this.currentMusicPlayer);
            }
        }

        this.customMusicInstances.values().removeIf(AbstractTickableSoundInstance::isStopped);
        if (this.currentMusicPlayer != this.previousMusicPlayer || this.currentMusic != this.previousMusic) {
            this.previousMusicPlayer = this.currentMusicPlayer;
            this.previousMusic = this.currentMusic;
            this.customMusicInstances.forEach((customMusic, instance) -> {
                COTWCommon.debug(Constants.DEBUG_CUSTOM_MUSIC, "Fading out custom music {}", customMusic.music().getLocation());
                instance.fadeOut();
            });
            if(this.currentMusicPlayer != null && this.currentMusic != null){
                this.customMusicInstances.compute(this.currentMusic, (customMusic, soundInstance) -> {
                    if (soundInstance == null) {
                        soundInstance = new CustomMusicSoundInstance<>(this.currentMusicPlayer, customMusic);
                        this.soundManager.play(soundInstance);
                    }
                    COTWCommon.debug(Constants.DEBUG_CUSTOM_MUSIC, "Fading in custom music {}", this.currentMusic.music().getLocation());
                    soundInstance.fadeIn();
                    return soundInstance;
                });
            }
        }
    }

    public static class CustomMusicSoundInstance<T extends Entity & CustomMusicPlayer> extends AbstractTickableSoundInstance {
        private final T musicPlayer;
        private int fadeDirection;
        private int fade;

        public CustomMusicSoundInstance(T musicPlayer, CustomMusicPlayer.CustomMusicData customMusic) {
            super(customMusic.music(), customMusic.source(), customMusic.random());
            this.musicPlayer = musicPlayer;
            this.looping = customMusic.looping();
            this.delay = customMusic.delay();
            this.volume = customMusic.volume();
            this.attenuation = customMusic.attenuation();
            this.relative = customMusic.relative();
            this.x = musicPlayer.getX();
            this.y = musicPlayer.getY();
            this.z = musicPlayer.getZ();
        }

        @Override
        public boolean canPlaySound() {
            return !this.musicPlayer.isSilent();
        }

        @Override
        public void tick() {
            if(this.musicPlayer.isAlive()){
                this.x = this.musicPlayer.getX();
                this.y = this.musicPlayer.getY();
                this.z = this.musicPlayer.getZ();
            } else{
                this.fadeOut();
            }

            if (this.fade < 0) {
                this.stop();
            }

            this.fade += this.fadeDirection;
            this.volume = Mth.clamp((float)this.fade / LOOP_SOUND_CROSS_FADE_TIME, 0.0F, 1.0F);
        }

        public void fadeOut() {
            this.fade = Math.min(this.fade, LOOP_SOUND_CROSS_FADE_TIME);
            this.fadeDirection = -1;
        }

        public void fadeIn() {
            this.fade = Math.max(0, this.fade);
            this.fadeDirection = 1;
        }
    }
}