package com.Thelnfamous1.craft_of_the_wild.event;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.network.COTWForgeNetwork;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MODID)
public class CommonModEvents {


    @SubscribeEvent
    static void attribs(EntityAttributeCreationEvent e) {
        EntityInit.attributeSuppliers.forEach(p -> e.put(p.entityTypeSupplier().get(), p.factory().get().build()));
    }

    @SubscribeEvent
    static void commonSetup(FMLCommonSetupEvent event){
        event.enqueueWork(COTWCommon::registerSpawnEggs);
        event.enqueueWork(COTWForgeNetwork::init);
    }

    @SubscribeEvent
    static void onSpawnPlacementRegistration(SpawnPlacementRegisterEvent event){
        event.register(EntityInit.STONE_TALUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                StoneTalus::checkStoneTalusSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
    }
}
