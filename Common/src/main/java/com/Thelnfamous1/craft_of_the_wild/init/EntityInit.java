package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.Beedle;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusArm;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EntityInit {
    public static final RegistrationProvider<EntityType<?>> ENTITIES = RegistrationProvider.get(Registries.ENTITY_TYPE, Constants.MODID);
    public static final List<AttributesRegister<?>> attributeSuppliers = new ArrayList<>();

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> supplier) {
        return ENTITIES.register(name, () -> supplier.get().build(Constants.MODID + ":" + name));
    }

    public static final RegistryObject<EntityType<StoneTalus>> STONE_TALUS = registerEntity("stone_talus", () ->
            EntityType.Builder.of(StoneTalus::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(3.125F * StoneTalus.LOGICAL_SCALE, 2.9375F * StoneTalus.LOGICAL_SCALE)
                    .clientTrackingRange(10),
            StoneTalus::createAttributes);

    public static final RegistryObject<EntityType<StoneTalusArm>> STONE_TALUS_ARM = registerEntity("stone_talus_arm", () ->
                    EntityType.Builder.<StoneTalusArm>of(StoneTalusArm::new, MobCategory.MISC)
                            .fireImmune()
                            .sized(1.5625F * StoneTalus.LOGICAL_SCALE, 1.125F * StoneTalus.LOGICAL_SCALE) // the arm will be horizontal when thrown, so flip the width/height from the part entity
                            .clientTrackingRange(4)
                            .updateInterval(10));

    // Beedle height is 36/16
    // Beedle width is 14/16
    // Beedle with backpack height is 47/16
    // Beedle with backpack width is 44/16
    public static final RegistryObject<EntityType<Beedle>> BEEDLE = registerEntity("beedle", () ->
                    EntityType.Builder.of(Beedle::new, MobCategory.MISC)
                            .sized(pixelsToBlocks(44.0F), pixelsToBlocks(47.0F))
                            .clientTrackingRange(10),
            Beedle::createAttributes);

    public static final EntityDimensions BEEDLE_SITTING = EntityDimensions.scalable(pixelsToBlocks(44.0F), pixelsToBlocks(29.0F));

    public static final EntityDimensions BEEDLE_DYING = EntityDimensions.scalable(pixelsToBlocks(44.0F), pixelsToBlocks(27.0F));

    private static <T extends LivingEntity> RegistryObject<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> supplier,
                                                                                         Supplier<AttributeSupplier.Builder> attributeSupplier) {
        RegistryObject<EntityType<T>> entityTypeSupplier = registerEntity(name, supplier);
        attributeSuppliers.add(new AttributesRegister<>(entityTypeSupplier, attributeSupplier));
        return entityTypeSupplier;
    }

    public static float pixelsToBlocks(float pixels){
        return pixels / 16.0F;
    }

    public static void loadClass() {
    }


    public record AttributesRegister<E extends LivingEntity>(Supplier<EntityType<E>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> factory) {}
}
