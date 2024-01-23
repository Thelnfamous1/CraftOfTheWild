package com.Thelnfamous1.craft_of_the_wild.platform;

import com.Thelnfamous1.craft_of_the_wild.entity.COTWMultipartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.PartEntityController;
import com.Thelnfamous1.craft_of_the_wild.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

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
}
