package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.trader.Beedle;
import com.Thelnfamous1.craft_of_the_wild.entity.Kass;
import com.Thelnfamous1.craft_of_the_wild.entity.pebblit.FrostPebblit;
import com.Thelnfamous1.craft_of_the_wild.entity.pebblit.IgneoPebblit;
import com.Thelnfamous1.craft_of_the_wild.entity.pebblit.StonePebblit;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.*;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.FrostTalusArm;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.IgneoTalusArm;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.StoneTalusArm;
import com.Thelnfamous1.craft_of_the_wild.entity.trader.Kilton;
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

    public static final RegistryObject<EntityType<StoneTalus>> STONE_TALUS = registerTalus("stone_talus", StoneTalus::new, StoneTalus::createAttributes);

    public static final RegistryObject<EntityType<FrostTalus>> FROST_TALUS = registerTalus("frost_talus", FrostTalus::new, FrostTalus::createAttributes);

    public static final RegistryObject<EntityType<IgneoTalus>> IGNEO_TALUS = registerTalus("igneo_talus", IgneoTalus::new, IgneoTalus::createAttributes);

    public static final RegistryObject<EntityType<StoneTalusRare>> STONE_TALUS_RARE = registerTalus("stone_talus_rare", StoneTalusRare::new, StoneTalusRare::createAttributes);

    public static final RegistryObject<EntityType<StoneTalusLuminous>> STONE_TALUS_LUMINOUS = registerTalus("stone_talus_luminous", StoneTalusLuminous::new, StoneTalusLuminous::createAttributes);

    private static <T extends StoneTalus> RegistryObject<EntityType<T>> registerTalus(String talusName, EntityType.EntityFactory<T> factory, Supplier<AttributeSupplier.Builder> talusAttributes) {
        return registerEntity(talusName, () ->
                        EntityType.Builder.of(factory, MobCategory.MONSTER)
                                .fireImmune()
                                .sized(3.125F * StoneTalus.LOGICAL_SCALE, 2.9375F * StoneTalus.LOGICAL_SCALE)
                                .clientTrackingRange(10),
                talusAttributes);
    }

    public static final RegistryObject<EntityType<StoneTalusArm>> STONE_TALUS_ARM = registerTalusArm("stone_talus_arm", StoneTalusArm::new);

    public static final RegistryObject<EntityType<StoneTalusArm>> FROST_TALUS_ARM = registerTalusArm("frost_talus_arm", FrostTalusArm::new);

    public static final RegistryObject<EntityType<StoneTalusArm>> IGNEO_TALUS_ARM = registerTalusArm("igneo_talus_arm", IgneoTalusArm::new);

    private static <T extends StoneTalusArm> RegistryObject<EntityType<T>> registerTalusArm(String talusArmName, EntityType.EntityFactory<T> factory) {
        return registerEntity(talusArmName, () ->
                EntityType.Builder.<T>of(factory, MobCategory.MISC)
                        .fireImmune()
                        .sized(1.5625F * StoneTalus.LOGICAL_SCALE, 1.125F * StoneTalus.LOGICAL_SCALE) // the arm will be horizontal when thrown, so flip the width/height from the part entity
                        .clientTrackingRange(4)
                        .updateInterval(10));
    }

    // Beedle height is 36/16
    // Beedle width is 14/16
    // Beedle with backpack height is 47/16
    // Beedle with backpack width is 44/16
    public static final RegistryObject<EntityType<Beedle>> BEEDLE = registerEntity("beedle", () ->
                    EntityType.Builder.of(Beedle::new, MobCategory.MISC)
                            .sized(0.6F, 1.95F) // same dimensions as Villager
                            .clientTrackingRange(10),
            Beedle::createAttributes);

    public static final EntityDimensions BEEDLE_SITTING = EntityDimensions.scalable(0.6F, 1.4F);

    public static final EntityDimensions BEEDLE_DYING = EntityDimensions.scalable(1.95F, 0.6F);

    public static final RegistryObject<EntityType<Kass>> KASS = registerEntity("kass", () ->
                    EntityType.Builder.of(Kass::new, MobCategory.MISC)
                            .sized(pixelsToBlocks(16.0F), pixelsToBlocks(51.0F)) // same dimensions as Villager
                            .clientTrackingRange(10),
            Kass::createAttributes);

    private static <T extends LivingEntity> RegistryObject<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> supplier,
                                                                                         Supplier<AttributeSupplier.Builder> attributeSupplier) {
        RegistryObject<EntityType<T>> entityTypeSupplier = registerEntity(name, supplier);
        attributeSuppliers.add(new AttributesRegister<>(entityTypeSupplier, attributeSupplier));
        return entityTypeSupplier;
    }

    public static final RegistryObject<EntityType<StonePebblit>> STONE_PEBBLIT = registerPebblit("stone_pebblit", StonePebblit::new, StonePebblit::createAttributes);

    public static final RegistryObject<EntityType<FrostPebblit>> FROST_PEBBLIT = registerPebblit("frost_pebblit", FrostPebblit::new, FrostPebblit::createAttributes);

    public static final RegistryObject<EntityType<IgneoPebblit>> IGNEO_PEBBLIT = registerPebblit("igneo_pebblit", IgneoPebblit::new, IgneoPebblit::createAttributes);

    private static <T extends StonePebblit> RegistryObject<EntityType<T>> registerPebblit(String pebblitName, EntityType.EntityFactory<T> factory, Supplier<AttributeSupplier.Builder> pebblitAttributes) {
        return registerEntity(pebblitName, () ->
                        EntityType.Builder.of(factory, MobCategory.MONSTER)
                                .fireImmune()
                                .sized(pixelsToBlocks(31.0F) * StonePebblit.LOGICAL_SCALE, pixelsToBlocks(31.0F) * StonePebblit.LOGICAL_SCALE)
                                .clientTrackingRange(10),
                pebblitAttributes);
    }


    public static final float KILTON_SCALE = 0.75F;
    // Kilton height is 34/16
    // Kilton width is 12/16
    // He has to be scaled down to one quarter of that to be 1/2 (8/16) block tall
    public static final RegistryObject<EntityType<Kilton>> KILTON = registerEntity("kilton", () ->
                    EntityType.Builder.of(Kilton::new, MobCategory.MISC)
                            .sized(pixelsToBlocks(12.0F) * KILTON_SCALE, pixelsToBlocks(34.0F) * KILTON_SCALE)
                            .clientTrackingRange(10),
            Kilton::createAttributes);

    public static final EntityDimensions KILTON_DYING = EntityDimensions.scalable(pixelsToBlocks(34.0F) * KILTON_SCALE, pixelsToBlocks(12.0F) * KILTON_SCALE);

    public static float pixelsToBlocks(float pixels){
        return pixels / 16.0F;
    }

    public static void loadClass() {
    }


    public record AttributesRegister<E extends LivingEntity>(Supplier<EntityType<E>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> factory) {}
}
