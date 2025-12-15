package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
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

import java.util.stream.Stream;

public class ModEntityLootTables extends EntityLootSubProvider {
    protected ModEntityLootTables() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        this.add(EntityInit.STONE_TALUS.get(), this.chanceToDrop(
                new LootEntry(Items.RAW_IRON, UniformGenerator.between(4, 6), 1.0F),
                new LootEntry(Items.RAW_GOLD, UniformGenerator.between(3, 6), 1.0F),
                new LootEntry(Items.LAPIS_LAZULI, UniformGenerator.between(1, 4), 1.0F),
                new LootEntry(Items.DIAMOND, UniformGenerator.between(1, 4), 0.2F),
                new LootEntry(ItemInit.MUSIC_DISC_A_ROCKY_BALLAD.get(), ConstantValue.exactly(1.0F), 0.05F)));
        this.add(EntityInit.FROST_TALUS.get(), this.chanceToDrop(
                new LootEntry(Items.RAW_IRON, UniformGenerator.between(2, 4), 1.0F),
                new LootEntry(Items.RAW_GOLD, UniformGenerator.between(2, 3), 1.0F),
                new LootEntry(Items.LAPIS_LAZULI, UniformGenerator.between(1, 3), 1.0F),
                new LootEntry(Items.DIAMOND, UniformGenerator.between(3, 6), 1.0F),
                new LootEntry(ItemInit.MUSIC_DISC_A_ROCKY_BALLAD.get(), ConstantValue.exactly(1.0F), 0.25F)));
        this.add(EntityInit.IGNEO_TALUS.get(), this.chanceToDrop(
                new LootEntry(Items.RAW_IRON, UniformGenerator.between(2, 4), 1.0F),
                new LootEntry(Items.RAW_GOLD, UniformGenerator.between(2, 3), 1.0F),
                new LootEntry(Items.LAPIS_LAZULI, UniformGenerator.between(1, 3), 1.0F),
                new LootEntry(Items.NETHERITE_SCRAP, UniformGenerator.between(2, 4), 1.0F),
                new LootEntry(ItemInit.MUSIC_DISC_A_ROCKY_BALLAD.get(), ConstantValue.exactly(1.0F), 0.20F)));
        this.add(EntityInit.STONE_TALUS_RARE.get(), this.chanceToDrop(
                new LootEntry(Items.RAW_IRON, UniformGenerator.between(2, 4), 1.0F),
                new LootEntry(Items.RAW_GOLD, UniformGenerator.between(2, 3), 1.0F),
                new LootEntry(Items.LAPIS_LAZULI, UniformGenerator.between(1, 3), 1.0F),
                new LootEntry(Items.DIAMOND, UniformGenerator.between(2, 4), 1.0F),
                new LootEntry(ItemInit.MUSIC_DISC_A_ROCKY_BALLAD.get(), ConstantValue.exactly(1.0F), 0.15F)));
        this.add(EntityInit.STONE_TALUS_LUMINOUS.get(), this.chanceToDrop(
                new LootEntry(Items.RAW_IRON, UniformGenerator.between(2, 4), 1.0F),
                new LootEntry(Items.RAW_GOLD, UniformGenerator.between(2, 3), 1.0F),
                new LootEntry(Items.LAPIS_LAZULI, UniformGenerator.between(4, 7), 1.0F),
                new LootEntry(ItemInit.LUMINOUS_STONE.get(), UniformGenerator.between(4, 6), 1.0F),
                new LootEntry(ItemInit.MUSIC_DISC_A_ROCKY_BALLAD.get(), ConstantValue.exactly(1.0F), 0.15F)));
        this.dropSingle(EntityInit.BEEDLE.get(), Items.EMERALD);
        this.dropSingle(EntityInit.KASS.get(), Items.FEATHER);
        this.dropSingle(EntityInit.STONE_PEBBLIT.get(), Items.COBBLESTONE);
        this.dropSingle(EntityInit.FROST_PEBBLIT.get(), Items.BLUE_ICE);
        this.dropSingle(EntityInit.IGNEO_PEBBLIT.get(), Items.MAGMA_BLOCK);
    }

    @Override
    protected boolean canHaveLootTable(EntityType<?> pEntityType) {
        if(pEntityType == EntityInit.BEEDLE.get() || pEntityType == EntityInit.KASS.get()){
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

    private LootTable.Builder chanceToDrop(LootEntry... entries) {
        LootTable.Builder lootTable = LootTable.lootTable();
        for (LootEntry entry : entries) {
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

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return EntityInit.ENTITIES.getEntries().stream().map(RegistryObject::get);
    }

    record LootEntry(Item item, NumberProvider numberProvider, float chance) {}
}
