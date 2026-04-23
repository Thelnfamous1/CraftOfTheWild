package com.Thelnfamous1.craft_of_the_wild.item;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.function.Supplier;

public enum COTWArmorMaterials  implements StringRepresentable, ArmorMaterial{
    RADIANT("radiant", 15, Util.make(new EnumMap<>(ArmorItem.Type.class), (armorMultipliers) -> {
        armorMultipliers.put(ArmorItem.Type.BOOTS, 2);
        armorMultipliers.put(ArmorItem.Type.LEGGINGS, 5);
        armorMultipliers.put(ArmorItem.Type.CHESTPLATE, 6);
        armorMultipliers.put(ArmorItem.Type.HELMET, 2);
    }), 9, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(ItemInit.LUMINOUS_STONE.get())),
    HYLIAN("hylian", 15, Util.make(new EnumMap<>(ArmorItem.Type.class), (armorMultipliers) -> {
        armorMultipliers.put(ArmorItem.Type.BOOTS, 2);
        armorMultipliers.put(ArmorItem.Type.LEGGINGS, 5);
        armorMultipliers.put(ArmorItem.Type.CHESTPLATE, 6);
        armorMultipliers.put(ArmorItem.Type.HELMET, 2);
    }), 9, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Items.IRON_INGOT)),
    BOKOBLIN("bokoblin", 7, Util.make(new EnumMap<>(ArmorItem.Type.class), (armorMultipliers) -> {
        armorMultipliers.put(ArmorItem.Type.BOOTS, 1);
        armorMultipliers.put(ArmorItem.Type.LEGGINGS, 3);
        armorMultipliers.put(ArmorItem.Type.CHESTPLATE, 5);
        armorMultipliers.put(ArmorItem.Type.HELMET, 2);
    }), 9, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(ItemInit.LUMINOUS_STONE.get()));

    public static final StringRepresentable.EnumCodec<ArmorMaterials> CODEC = StringRepresentable.fromEnum(ArmorMaterials::values);
    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), ($$0) -> {
        $$0.put(ArmorItem.Type.BOOTS, 13);
        $$0.put(ArmorItem.Type.LEGGINGS, 15);
        $$0.put(ArmorItem.Type.CHESTPLATE, 16);
        $$0.put(ArmorItem.Type.HELMET, 11);
    });
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    COTWArmorMaterials(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> protectionFunctionForType, int enchantmentValue, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = COTWCommon.getResourceLocation(name).toString();
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionFunctionForType = protectionFunctionForType;
        this.enchantmentValue = enchantmentValue;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return HEALTH_FUNCTION_FOR_TYPE.get(type) * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.protectionFunctionForType.get(type);
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
