package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWLootTables;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ModChestLootTables implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> saver) {
        saver.accept(COTWLootTables.STABLES_V1_MAIN, stablesMainLoot());
        saver.accept(COTWLootTables.STABLES_V1_MUSIC_DISC, singleItem(ItemInit.MUSIC_DISC_STABLES.get()));
        saver.accept(COTWLootTables.STABLES_V1_BOX, stablesBoxLoot());
        saver.accept(COTWLootTables.STABLES_V2_MAIN, stablesMainLoot());
        saver.accept(COTWLootTables.STABLES_V2_MUSIC_DISC, singleItem(ItemInit.MUSIC_DISC_STABLES.get()));
        saver.accept(COTWLootTables.STABLES_V2_BOX, stablesBoxLoot());
        saver.accept(COTWLootTables.STABLES_V3_MAIN, stablesMainLoot());
        saver.accept(COTWLootTables.STABLES_V3_MUSIC_DISC, singleItem(ItemInit.MUSIC_DISC_STABLES.get()));
        saver.accept(COTWLootTables.STABLES_V3_BOX, stablesBoxLoot());
        saver.accept(COTWLootTables.STABLES_V4_MAIN, stablesMainLoot());
        saver.accept(COTWLootTables.STABLES_V4_MUSIC_DISC, singleItem(ItemInit.MUSIC_DISC_STABLES.get()));
        saver.accept(COTWLootTables.STABLES_V4_BOX, stablesBoxLoot());
        saver.accept(COTWLootTables.STABLES_V4_WELL, singleItem(ItemInit.MUSIC_DISC_LOST_CITY.get()));
        // SNOWY STABLES
        saver.accept(COTWLootTables.STABLES_V1_SNOWY_MAIN, stablesMainLoot());
        saver.accept(COTWLootTables.STABLES_V1_SNOWY_MUSIC_DISC, singleItem(ItemInit.MUSIC_DISC_STABLES.get()));
        saver.accept(COTWLootTables.STABLES_V1_SNOWY_BOX, stablesBoxLoot());
        saver.accept(COTWLootTables.STABLES_V2_SNOWY_MAIN, stablesMainLoot());
        saver.accept(COTWLootTables.STABLES_V2_SNOWY_MUSIC_DISC, singleItem(ItemInit.MUSIC_DISC_STABLES.get()));
        saver.accept(COTWLootTables.STABLES_V2_SNOWY_BOX, stablesBoxLoot());
        saver.accept(COTWLootTables.STABLES_V3_SNOWY_MAIN, stablesMainLoot());
        saver.accept(COTWLootTables.STABLES_V3_SNOWY_MUSIC_DISC, singleItem(ItemInit.MUSIC_DISC_STABLES.get()));
        saver.accept(COTWLootTables.STABLES_V3_SNOWY_BOX, stablesBoxLoot());
        saver.accept(COTWLootTables.STABLES_V4_SNOWY_MAIN, stablesMainLoot());
        saver.accept(COTWLootTables.STABLES_V4_SNOWY_MUSIC_DISC, singleItem(ItemInit.MUSIC_DISC_STABLES.get()));
        saver.accept(COTWLootTables.STABLES_V4_SNOWY_BOX, stablesBoxLoot());
        saver.accept(COTWLootTables.STABLES_V4_SNOWY_WELL, singleItem(ItemInit.MUSIC_DISC_LOST_CITY.get()));
    }

    private static LootTable.Builder singleItem(Item item) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(item)));
    }

    private LootTable.Builder stablesMainLoot() {
        return this.chanceToDrop(
                new LootEntry(Items.SADDLE, UniformGenerator.between(1.0F, 2.0F), 1.0F),
                new LootEntry(Items.IRON_HORSE_ARMOR, UniformGenerator.between(1.0F, 2.0F), 0.19F),
                new LootEntry(Items.GOLDEN_HORSE_ARMOR, UniformGenerator.between(1.0F, 2.0F), 0.057F),
                new LootEntry(Items.DIAMOND_HORSE_ARMOR, ConstantValue.exactly(1.0F), 0.08F));
    }

    private LootTable.Builder chanceToDrop(ModChestLootTables.LootEntry... entries) {
        LootTable.Builder lootTable = LootTable.lootTable();
        for (ModChestLootTables.LootEntry entry : entries) {
            int weight = Mth.floor(entry.chance * 1000.0F);
            LootPool.Builder pool = LootPool.lootPool();
            pool.setRolls(ConstantValue.exactly(1));
            pool.add(LootItem.lootTableItem(entry.item())
                    .setWeight(weight)
                    .apply(SetItemCountFunction.setCount(entry.numberProvider())));
            if(weight < 1000){
                pool.add(EmptyLootItem.emptyItem()
                        .setWeight(1000 - weight));
            }
            lootTable.withPool(pool);
        }
        return lootTable;
    }

    private LootTable.Builder stablesBoxLoot() {
        return this.multiPool(
                new LootEntry(Items.ARROW, UniformGenerator.between(2.0F, 4.0F), 1.0F),
                new LootEntry(Items.APPLE, UniformGenerator.between(1.0F, 3.0F), 1.0F),
                new LootEntry(Items.EMERALD, UniformGenerator.between(1.0F, 2.0F), 1.0F));
    }

    private LootTable.Builder multiPool(LootEntry... entries) {
        LootTable.Builder lootTable = LootTable.lootTable();
        for (ModChestLootTables.LootEntry entry : entries) {
            LootPool.Builder pool = LootPool.lootPool();
            pool.setRolls(ConstantValue.exactly(1));
            pool.add(LootItem.lootTableItem(entry.item()).apply(SetItemCountFunction.setCount(entry.numberProvider())));
            lootTable.withPool(pool);
        }
        return lootTable;
    }

    record LootEntry(Item item, NumberProvider numberProvider, float chance) {}
}
