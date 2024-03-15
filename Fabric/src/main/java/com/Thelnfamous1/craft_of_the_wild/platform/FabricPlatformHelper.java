package com.Thelnfamous1.craft_of_the_wild.platform;

import com.Thelnfamous1.craft_of_the_wild.entity.COTWMultipartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.PartEntityController;
import com.Thelnfamous1.craft_of_the_wild.network.S2CCircleParticlesPacket;
import com.Thelnfamous1.craft_of_the_wild.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

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
        ClientPlayNetworking.send(new S2CCircleParticlesPacket(particle, x, y, z, xZRadius, count));
    }

    @Override
    public void onFinalizeSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance currentDifficultyAt, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CompoundTag saveData) {
        mob.finalizeSpawn(level, currentDifficultyAt, spawnType, spawnGroupData, saveData);
    }
}
