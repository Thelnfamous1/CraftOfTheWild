package com.Thelnfamous1.craft_of_the_wild.platform;

import com.Thelnfamous1.craft_of_the_wild.entity.COTWForgePartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.COTWMultipartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.PartEntityController;
import com.Thelnfamous1.craft_of_the_wild.network.COTWForgeNetwork;
import com.Thelnfamous1.craft_of_the_wild.network.ClientboundCircleParticlesPacket;
import com.Thelnfamous1.craft_of_the_wild.platform.services.IPlatformHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;

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
    public <P extends LivingEntity & COTWMultipartEntity> PartEntityController<P, ? extends Entity> makePartEntityController(P parent, PartEntityController.PartTicker<P, Entity> partTicker, PartEntityController.PartResizer<P> partResizer, PartEntityController.PartInfo... partInfos) {
        PartEntityController.Builder<P, COTWForgePartEntity<P>> builder = new PartEntityController.Builder<P, COTWForgePartEntity<P>>(parent)
                .useNameProvider(COTWForgePartEntity::getPartName);
        for(PartEntityController.PartInfo partInfo : partInfos){
            builder.addPart(new COTWForgePartEntity<>(parent, partInfo, partResizer), partInfo);
        }
        builder.universalTicker(COTWForgePartEntity.typedPartTicker(partTicker));
        return builder.build();
    }

    @Override
    public boolean canEntityGrief(Level level, Entity entity) {
        return ForgeEventFactory.getMobGriefingEvent(level, entity);
    }

    @Override
    public <T extends ParticleOptions> void sendCircleParticlesPacket(T particle, double x, double y, double z, double xZRadius, int count) {
        COTWForgeNetwork.SYNC_CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundCircleParticlesPacket(particle, x, y, z, xZRadius, count));
    }
}