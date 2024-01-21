package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class AttributeInit {
    public static final RegistrationProvider<Attribute> ATTRIBUTES = RegistrationProvider.get(Registries.ATTRIBUTE, Constants.MODID);

    public static final RegistryObject<Attribute> PROJECTILE_RESISTANCE = getRegister("generic.projectile_resistance");

    private static RegistryObject<Attribute> getRegister(String path) {
        return ATTRIBUTES.register(path, () ->
                new RangedAttribute("attribute.name.%s.%s".formatted(Constants.MODID, path),
                        0.0, 0.0, 1.0));
    }

    public static void loadClass() {

    }
}
