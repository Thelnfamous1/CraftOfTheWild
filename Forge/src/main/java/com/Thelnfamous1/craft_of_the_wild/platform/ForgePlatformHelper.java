package com.Thelnfamous1.craft_of_the_wild.platform;

import com.Thelnfamous1.craft_of_the_wild.entity.COTWPartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.MultipartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.PartEntityController;
import com.Thelnfamous1.craft_of_the_wild.platform.services.IPlatformHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public <P extends LivingEntity & MultipartEntity> PartEntityController<? extends Entity> makePartEntityController(P parent, PartEntityController.Info... infos) {
        PartEntityController.Builder<COTWPartEntity<P>> builder = new PartEntityController.Builder<COTWPartEntity<P>>()
                .useNameProvider(COTWPartEntity::getPartName);
        for(PartEntityController.Info info : infos){
            builder.addPart(new COTWPartEntity<>(parent, info));
        }
        builder.universalTicker(COTWPartEntity::basicTicker);
        return builder.build();
    }
}