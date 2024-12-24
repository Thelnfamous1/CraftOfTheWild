package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.item.COTWRecordItem;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;

import java.util.Objects;

public class ItemInit {
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MODID);
    public static final RegistrationProvider<CreativeModeTab> CREATIVE_MODE_TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, Constants.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(Constants.MODID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(Objects.requireNonNull(
                    ITEMS.getRegistry().get(COTWCommon.getResourceLocation("music_disc_of_the_wild")))))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        ITEMS.getEntries().forEach((registryObject) -> output.accept(new ItemStack(registryObject.get())));
                    }).title(Component.translatable("itemGroup." + Constants.MODID + ".tab"))
            .build());

    public static final RegistryObject<Item> STONE_TALUS_SPAWN_EGG = ITEMS.register("stone_talus_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.STONE_TALUS, 14405058, 7643954, getItemProperties()));

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
