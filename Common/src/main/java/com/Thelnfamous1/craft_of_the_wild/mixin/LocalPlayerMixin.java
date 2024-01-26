package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.client.sound.BossMusicSoundHandler;
import com.Thelnfamous1.craft_of_the_wild.duck.BossMusicListener;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements BossMusicListener {
    @Unique
    private BossMusicSoundHandler craft_of_the_wild$bossMusicSoundHandler;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void handleInit(Minecraft pMinecraft, ClientLevel pClientLevel, ClientPacketListener pConnection, StatsCounter pStats, ClientRecipeBook pRecipeBook, boolean pWasShiftKeyDown, boolean pWasSprinting, CallbackInfo ci){
        this.craft_of_the_wild$bossMusicSoundHandler = new BossMusicSoundHandler((LocalPlayer) (Object) this, pMinecraft.getSoundManager());
    }

    @Override
    public BossMusicSoundHandler craft_of_the_wild$getBossMusicSoundHandler() {
        return this.craft_of_the_wild$bossMusicSoundHandler;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void handleTick(CallbackInfo ci){
        this.craft_of_the_wild$bossMusicSoundHandler.tick();
    }
}
