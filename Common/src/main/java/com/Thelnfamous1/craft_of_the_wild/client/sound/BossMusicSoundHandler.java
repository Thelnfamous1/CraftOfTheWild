package com.Thelnfamous1.craft_of_the_wild.client.sound;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class BossMusicSoundHandler {
    private static final int LOOP_SOUND_CROSS_FADE_TIME = 40;
    private final LocalPlayer player;
    private final SoundManager soundManager;
    private final Object2ObjectArrayMap<SoundEvent, BossMusicSoundInstance> bossMusicSoundInstances = new Object2ObjectArrayMap<>();
    @Nullable
    private LivingEntity previousBoss;
    @Nullable
    private LivingEntity currentBoss;
    @Nullable
    private SoundEvent currentBossMusic;

    public BossMusicSoundHandler(LocalPlayer player, SoundManager soundManager) {
        this.player = player;
        this.soundManager = soundManager;
    }

    public void playBossMusicFor(LivingEntity boss, SoundEvent bossMusic){
        if(this.currentBoss != boss && boss.isAlive() && this.canPlayerHearBossMusic(boss)){
            COTWCommon.debug(Constants.DEBUG_BOSS_MUSIC, "Playing boss music for {}", boss);
            this.currentBoss = boss;
            this.currentBossMusic = bossMusic;
        }
    }

    public void stopBossMusicFor(LivingEntity boss){
        if(this.currentBoss == boss){
            COTWCommon.debug(Constants.DEBUG_BOSS_MUSIC, "Stopping boss music for {}", boss);
            this.currentBoss = null;
            this.currentBossMusic = null;
        }
    }

    public void tick() {
        if (this.currentBoss != null) {
            if (this.currentBoss.isAlive()) {
                if(!this.canPlayerHearBossMusic(this.currentBoss)){
                    COTWCommon.debug(Constants.DEBUG_BOSS_MUSIC, "{} cannot hear boss music for {}", this.player, this.currentBoss);
                    this.stopBossMusicFor(this.currentBoss);
                }
            } else{
                this.stopBossMusicFor(this.currentBoss);
            }
        }

        this.bossMusicSoundInstances.values().removeIf(AbstractTickableSoundInstance::isStopped);
        if (this.currentBoss != this.previousBoss) {
            this.previousBoss = this.currentBoss;
            this.bossMusicSoundInstances.forEach((bossMusic, bossMusicInstance) -> {
                COTWCommon.debug(Constants.DEBUG_BOSS_MUSIC, "Fading out boss music {}", bossMusic.getLocation());
                bossMusicInstance.fadeOut();
            });
            if(this.currentBoss != null && this.currentBossMusic != null){
                this.bossMusicSoundInstances.compute(this.currentBossMusic, (bossMusic, bossMusicInstance) -> {
                    if (bossMusicInstance == null) {
                        bossMusicInstance = new BossMusicSoundInstance(this.currentBoss, bossMusic);
                        this.soundManager.play(bossMusicInstance);
                    }
                    COTWCommon.debug(Constants.DEBUG_BOSS_MUSIC, "Fading in boss music {}", this.currentBossMusic.getLocation());
                    bossMusicInstance.fadeIn();
                    return bossMusicInstance;
                });
            }
        }
    }

    private boolean canPlayerHearBossMusic(LivingEntity boss) {
        return boss.canAttack(this.player) && boss.distanceToSqr(this.player) < Mth.square(64);
    }

    public static class BossMusicSoundInstance extends AbstractTickableSoundInstance {
        private final Entity boss;
        private int fadeDirection;
        private int fade;

        public BossMusicSoundInstance(Entity boss, SoundEvent bossMusic) {
            super(bossMusic, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
            this.boss = boss;
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0F;
            this.relative = true;
        }

        @Override
        public boolean canPlaySound() {
            return !this.boss.isSilent();
        }

        @Override
        public void tick() {
            if(this.boss.isAlive()){
                this.x = this.boss.getX();
                this.y = this.boss.getY();
                this.z = this.boss.getZ();
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