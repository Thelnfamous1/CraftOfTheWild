package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.google.common.collect.ImmutableSet;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;

public class VillagerProfessionInit {
    public static final RegistrationProvider<VillagerProfession> VILLAGER_PROFESSION = RegistrationProvider.get(Registries.VILLAGER_PROFESSION, Constants.MODID);

    public static final RegistryObject<VillagerProfession> BEEDLE = VILLAGER_PROFESSION.register("beedle", () -> new VillagerProfession("beedle", PoiType.NONE, PoiType.NONE, ImmutableSet.of(), ImmutableSet.of(), null));

    public static void loadClass(){}
}
