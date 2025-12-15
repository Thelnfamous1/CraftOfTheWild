package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.item.*;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ItemInit {
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MODID);
    public static final RegistrationProvider<CreativeModeTab> CREATIVE_MODE_TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, Constants.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(Constants.MODID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(Objects.requireNonNull(
                    ITEMS.getRegistry().get(COTWCommon.getResourceLocation("music_disc_of_the_wild")))))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        ITEMS.getEntries().forEach((registryObject) -> output.accept(new ItemStack(registryObject.get())));
                        generatePresetPaintings(output, (holder) -> {
                            return holder.is(PaintingVariantTags.PLACEABLE);
                        }, PaintingVariantInit.PAINTING_VARIANTS.getEntries().stream().map(paintingVariantRegistryObject -> itemDisplayParameters.holders().lookupOrThrow(Registries.PAINTING_VARIANT).getOrThrow(paintingVariantRegistryObject.getResourceKey())));
                    }).title(Component.translatable("itemGroup." + Constants.MODID + ".tab"))
            .build());

    private static final Comparator<Holder<PaintingVariant>> PAINTING_COMPARATOR = Comparator.comparing(Holder::value, Comparator.<PaintingVariant>comparingInt((p_270004_) -> {
        return p_270004_.getHeight() * p_270004_.getWidth();
    }).thenComparing(PaintingVariant::getWidth));

    private static void generatePresetPaintings(CreativeModeTab.Output pOutput, Predicate<Holder<PaintingVariant>> pPredicate, Stream<Holder.Reference<PaintingVariant>> referenceStream) {
        referenceStream.filter(pPredicate).sorted(PAINTING_COMPARATOR).forEach((paintingVariant) -> {
            ItemStack itemstack = new ItemStack(Items.PAINTING);
            CompoundTag compoundtag = itemstack.getOrCreateTagElement("EntityTag");
            Painting.storeVariant(compoundtag, paintingVariant);
            pOutput.accept(itemstack);
        });
    }

    public static final RegistryObject<Item> STONE_TALUS_SPAWN_EGG = ITEMS.register("stone_talus_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.STONE_TALUS, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> FROST_TALUS_SPAWN_EGG = ITEMS.register("frost_talus_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.FROST_TALUS, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> IGNEO_TALUS_SPAWN_EGG = ITEMS.register("igneo_talus_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.IGNEO_TALUS, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> STONE_TALUS_RARE_SPAWN_EGG = ITEMS.register("stone_talus_rare_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.STONE_TALUS_RARE, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> STONE_TALUS_LUMINOUS_SPAWN_EGG = ITEMS.register("stone_talus_luminous_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.STONE_TALUS_LUMINOUS, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> BEEDLE_SPAWN_EGG = ITEMS.register("beedle_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.BEEDLE, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> KASS_SPAWN_EGG = ITEMS.register("kass_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.KASS, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> MUSIC_DISC_A_ROCKY_BALLAD = registerMusicDisc(
            "music_disc_a_rocky_ballad", 14, SoundInit.MUSIC_A_ROCKY_BALLAD, 168, "Firel");
    public static final RegistryObject<Item> MUSIC_DISC_LOST_CITY = registerMusicDisc(
            "music_disc_lost_city", 14, SoundInit.MUSIC_LOST_CITY, 97, "Sophie Song");
    public static final RegistryObject<Item> MUSIC_DISC_OF_THE_WILD = registerMusicDisc(
            "music_disc_of_the_wild", 14, SoundInit.MUSIC_OF_THE_WILD, 66, "Sophie Song");
    public static final RegistryObject<Item> MUSIC_DISC_STABLES = registerMusicDisc(
            "music_disc_stables", 14, SoundInit.MUSIC_STABLES, 68, "Sophie Song");

    public static final RegistryObject<Item> EGG_PUDDING = ITEMS.register("egg_pudding", () ->
            new BowlFoodItem(getItemProperties().stacksTo(1).food(new FoodProperties.Builder().nutrition(10).saturationMod(0.6F).build())));

    public static final RegistryObject<Item> LUMINOUS_STONE = ITEMS.register("luminous_stone", () ->
            new RadiantItem(getItemProperties()));

    public static final RegistryObject<Item> RADIANT_HELMET = ITEMS.register("radiant_helmet", () ->
            new RadiantArmorItem(COTWArmorMaterials.RADIANT, ArmorItem.Type.HELMET, getItemProperties()));

    public static final RegistryObject<Item> RADIANT_CHESTPLATE = ITEMS.register("radiant_chestplate", () ->
            new RadiantArmorItem(COTWArmorMaterials.RADIANT, ArmorItem.Type.CHESTPLATE, getItemProperties()));

    public static final RegistryObject<Item> RADIANT_LEGGINGS = ITEMS.register("radiant_leggings", () ->
            new RadiantArmorItem(COTWArmorMaterials.RADIANT, ArmorItem.Type.LEGGINGS, getItemProperties()));

    public static final RegistryObject<Item> RADIANT_BOOTS = ITEMS.register("radiant_boots", () ->
            new RadiantArmorItem(COTWArmorMaterials.RADIANT, ArmorItem.Type.BOOTS, getItemProperties()));

    public static final RegistryObject<Item> MEDAL_OF_HONOR_TALUS = ITEMS.register("medal_of_honor_talus", () ->
            new Item(getItemProperties()));

    public static final RegistryObject<Item> MON = ITEMS.register("mon", () ->
            new Item(getItemProperties()));

    public static final RegistryObject<Item> BOKOBLIN_HELMET = ITEMS.register("bokoblin_helmet", () ->
            new BokoblinArmorItem(COTWArmorMaterials.BOKOBLIN, ArmorItem.Type.HELMET, getItemProperties()));

    public static final RegistryObject<Item> STONE_PEBBLIT_SPAWN_EGG = ITEMS.register("stone_pebblit_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.STONE_PEBBLIT, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> FROST_PEBBLIT_SPAWN_EGG = ITEMS.register("frost_pebblit_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.FROST_PEBBLIT, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> IGNEO_PEBBLIT_SPAWN_EGG = ITEMS.register("igneo_pebblit_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.IGNEO_PEBBLIT, 14405058, 7643954, getItemProperties()));

    private static RegistryObject<Item> registerMusicDisc(String path, int analogSignal, RegistryObject<SoundEvent> soundSupplier, int durationInSeconds, String author) {
        return ITEMS.register(path, () ->
                new COTWRecordItem(analogSignal, soundSupplier.get(), getItemProperties().stacksTo(1).rarity(Rarity.RARE), durationInSeconds, author));
    }

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }

    public static void loadClass() {
    }
}
