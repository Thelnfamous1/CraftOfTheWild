package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.feature.CustomFeaturePoolElement;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class StructurePoolElementTypeInit {

    public static final RegistrationProvider<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT_TYPES = RegistrationProvider.get(Registries.STRUCTURE_POOL_ELEMENT, Constants.MODID);

    public static final RegistryObject<StructurePoolElementType<CustomFeaturePoolElement>> CUSTOM_FEATURE = STRUCTURE_POOL_ELEMENT_TYPES.register("custom_feature_pool_element", () -> () -> CustomFeaturePoolElement.CODEC);

    public static void loadClass(){

    }
}