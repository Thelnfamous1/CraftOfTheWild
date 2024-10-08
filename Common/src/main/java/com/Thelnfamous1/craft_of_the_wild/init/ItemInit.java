package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import com.nyfaria.craft_of_the_wild.registration.RegistrationProvider;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemInit {
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MODID);
    public static final RegistrationProvider<CreativeModeTab> CREATIVE_MODE_TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, Constants.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(Constants.MODID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(Items.IRON_SWORD))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        ITEMS.getEntries().forEach((registryObject) -> output.accept(new ItemStack(registryObject.get())));
                    }).title(Component.translatable("itemGroup." + Constants.MODID + ".tab"))
            .build());

    public static final RegistryObject<Item> STONE_TALUS_SPAWN_EGG = ITEMS.register("stone_talus_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.STONE_TALUS, 14405058, 7643954, getItemProperties()));

    public static final RegistryObject<Item> BEEDLE_SPAWN_EGG = ITEMS.register("beedle_spawn_egg", () ->
            new COTWSpawnEggItem(EntityInit.BEEDLE, 14405058, 7643954, getItemProperties()));

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }

    public static void loadClass() {
    }
}
