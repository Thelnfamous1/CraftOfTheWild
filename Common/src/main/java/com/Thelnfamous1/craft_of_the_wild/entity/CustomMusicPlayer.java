package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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

    CustomMusicData getCustomMusic();

    boolean canPlayCustomMusic();

    boolean canCustomMusicBeHeardBy(Player player);
    
    record CustomMusicData(
            SoundEvent music, SoundSource source, 
            float volume, float pitch, 
            RandomSource random, 
            boolean looping, int delay, 
            Attenuation attenuation, 
            double x, double y, double z, 
            boolean relative){

        public CustomMusicData(SoundEvent pSoundEvent, SoundSource pSource, float pVolume, float pPitch, RandomSource pRandom, BlockPos pos) {
            this(pSoundEvent, pSource, pVolume, pPitch, pRandom, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
        }

        public static CustomMusicData forUI(SoundEvent pSound, float pPitch) {
            return forUI(pSound, pPitch, 0.25F);
        }

        public static CustomMusicData forUI(Holder<SoundEvent> pSoundHolder, float pPitch) {
            return forUI(pSoundHolder.value(), pPitch);
        }

        public static CustomMusicData forUI(SoundEvent pSound, float pPitch, float pVolume) {
            return new CustomMusicData(pSound, SoundSource.MASTER, pVolume, pPitch, createUnseededRandom(), false, 0, CustomMusicPlayer.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
        }

        public static CustomMusicData forMusic(SoundEvent pSound, boolean looping) {
            return new CustomMusicData(pSound, SoundSource.MUSIC, 1.0F, 1.0F, createUnseededRandom(), looping, 0, CustomMusicPlayer.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
        }

        public static CustomMusicData forRecord(SoundEvent pSound, Vec3 pPos, boolean pLooping) {
            return new CustomMusicData(pSound, SoundSource.RECORDS, 4.0F, 1.0F, createUnseededRandom(), pLooping, 0, CustomMusicPlayer.Attenuation.LINEAR, pPos.x, pPos.y, pPos.z);
        }

        public static CustomMusicData forLocalAmbience(SoundEvent pSound, float pVolume, float pPitch) {
            return new CustomMusicData(pSound, SoundSource.AMBIENT, pPitch, pVolume, createUnseededRandom(), false, 0, CustomMusicPlayer.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
        }

        public static CustomMusicData forAmbientAddition(SoundEvent pSound) {
            return forLocalAmbience(pSound, 1.0F, 1.0F);
        }

        public static CustomMusicData forAmbientMood(SoundEvent pSoundEvent, RandomSource pRandom, double pX, double pY, double pZ) {
            return new CustomMusicData(pSoundEvent, SoundSource.AMBIENT, 1.0F, 1.0F, pRandom, false, 0, CustomMusicPlayer.Attenuation.LINEAR, pX, pY, pZ);
        }

        public CustomMusicData(SoundEvent pSoundEvent, SoundSource pSource, float pVolume, float pPitch, RandomSource pRandom, double pX, double pY, double pZ) {
            this(pSoundEvent, pSource, pVolume, pPitch, pRandom, false, 0, CustomMusicPlayer.Attenuation.LINEAR, pX, pY, pZ);
        }

        private CustomMusicData(SoundEvent pSoundEvent, SoundSource pSource, float pVolume, float pPitch, RandomSource pRandom, boolean pLooping, int pDelay, CustomMusicPlayer.Attenuation pAttenuation, double pX, double pY, double pZ) {
            this(pSoundEvent, pSource, pVolume, pPitch, pRandom, pLooping, pDelay, pAttenuation, pX, pY, pZ, false);
        }

        static RandomSource createUnseededRandom() {
            return RandomSource.create();
        }
        
    }

    enum Attenuation {
        NONE,
        LINEAR;

        Attenuation() {
        }
    }
}
