package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

public class ModEntityLootTables extends EntityLootSubProvider {
    protected ModEntityLootTables() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        this.multiDrops(EntityInit.STONE_TALUS.get(),
                new LootEntry(Items.IRON_INGOT, ConstantValue.exactly(20)),
                new LootEntry(Items.GOLD_INGOT, ConstantValue.exactly(10)),
                new LootEntry(Items.LAPIS_LAZULI, ConstantValue.exactly(5)),
                new LootEntry(Items.DIAMOND, ConstantValue.exactly(3)));
        this.dropSingle(EntityInit.BEEDLE.get(), Items.EMERALD);
    }

    @Override
    protected boolean canHaveLootTable(EntityType<?> pEntityType) {
        if(pEntityType == EntityInit.BEEDLE.get()){
            return true;
        }
        return super.canHaveLootTable(pEntityType);
    }

    private void multiDrops(EntityType<?> type, LootEntry... entries) {
        LootPool.Builder pool = LootPool.lootPool();
        pool.setRolls(ConstantValue.exactly(1));
        for (LootEntry entry : entries) {
            pool.add(LootItem.lootTableItem(entry.item()).apply(SetItemCountFunction.setCount(entry.numberProvider())));
        }
        this.add(type, LootTable.lootTable().withPool(pool));
    }

    private void dropRange(EntityType<?> entityType, Item item, float min, float max) {
        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))));
        add(entityType, builder);
    }

    private void dropSingle(EntityType<?> entityType, Item item) {
        dropSetAmount(entityType, item, 1);
    }

    private void dropSetAmount(EntityType<?> entityType, Item item, float amount) {
        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(amount)))));
        add(entityType, builder);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return EntityInit.ENTITIES.getEntries().stream().map(RegistryObject::get);
    }

    record LootEntry(Item item, NumberProvider numberProvider) {}
}
