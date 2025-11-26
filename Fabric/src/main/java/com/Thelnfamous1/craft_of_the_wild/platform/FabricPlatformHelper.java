package com.Thelnfamous1.craft_of_the_wild.platform;

import com.Thelnfamous1.craft_of_the_wild.COTWFabric;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.compat.TrinketsCompat;
import com.Thelnfamous1.craft_of_the_wild.entity.COTWMultipartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.PartEntityController;
import com.Thelnfamous1.craft_of_the_wild.network.S2CCircleParticlesPacket;
import com.Thelnfamous1.craft_of_the_wild.network.S2CSyncDisguiseEffectCooldownsPacket;
import com.Thelnfamous1.craft_of_the_wild.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.Map;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <P extends LivingEntity & COTWMultipartEntity> PartEntityController<P, ? extends Entity> makePartEntityController(P parent, PartEntityController.PartTicker<P, Entity> partTicker, PartEntityController.PartResizer<P> partResizer, PartEntityController.PartInfo... partInfos) {
        return new PartEntityController.Builder<>(parent).build(); // TODO: Need a Fabric equivalent for Forge's PartEntity system
    }

    @Override
    public boolean canEntityGrief(Level level, Entity entity) {
        return level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    @Override
    public <T extends ParticleOptions> void sendCircleParticlesPacket(T particle, double x, double y, double z, double xZRadius, int count) {
        for(ServerPlayer serverPlayer : PlayerLookup.all(COTWFabric.getCurrentServer())){
            ServerPlayNetworking.send(serverPlayer, new S2CCircleParticlesPacket(particle, x, y, z, xZRadius, count));
        }
    }

    @Override
    public void onFinalizeSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance currentDifficultyAt, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CompoundTag saveData) {
        mob.finalizeSpawn(level, currentDifficultyAt, spawnType, spawnGroupData, saveData);
    }

    @Override
    public void sendSmashAttackParticlePacket(AABB attackBox, int power) {

    }

    @Override
    public void sendSyncDisguiseEffectPacket(Entity entity, Map<MobEffect, Integer> disguiseEffectCooldowns) {
        if(entity instanceof ServerPlayer self){
            ServerPlayNetworking.send(self, new S2CSyncDisguiseEffectCooldownsPacket(entity.getId(), disguiseEffectCooldowns));
        }
        for(ServerPlayer serverPlayer : PlayerLookup.tracking(entity)){
            ServerPlayNetworking.send(serverPlayer, new S2CSyncDisguiseEffectCooldownsPacket(entity.getId(), disguiseEffectCooldowns));
        }
    }

    @Override
    public boolean hasCharmEquipped(LivingEntity entity, Item item, String charmSlotId) {
        if(isModLoaded(Constants.TRINKETS_MODID)){
            return TrinketsCompat.hasCharmEquipped(entity, item, charmSlotId);
        }
        return false;
    }
}
