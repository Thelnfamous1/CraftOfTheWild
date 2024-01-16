package com.Thelnfamous1.craft_of_the_wild;

import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import com.Thelnfamous1.craft_of_the_wild.mixin.SpawnEggItemAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class COTWFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        COTWCommon.init();
        EntityInit.attributeSuppliers.forEach(
                p -> FabricDefaultAttributeRegistry.register(p.entityTypeSupplier().get(), p.factory().get().build())
        );
        COTWSpawnEggItem.getEggs()
                .forEach(egg -> SpawnEggItemAccessor.craft_of_the_wild$getBY_ID().put(egg.type(), egg));
    }
}
