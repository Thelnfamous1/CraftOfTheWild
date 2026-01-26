package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.init.DamageTypeInit;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModTagProvider {

    public static class ItemTags extends TagsProvider<Item>{

        public ItemTags(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, Registries.ITEM, lookupProvider, Constants.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(net.minecraft.tags.ItemTags.MUSIC_DISCS).add(
                    ItemInit.MUSIC_DISC_A_ROCKY_BALLAD.getResourceKey(),
                    ItemInit.MUSIC_DISC_LOST_CITY.getResourceKey(),
                    ItemInit.MUSIC_DISC_OF_THE_WILD.getResourceKey(),
                    ItemInit.MUSIC_DISC_STABLES.getResourceKey());
            this.populateTag(COTWTags.C418_MUSIC_DISCS,
                    () -> Items.MUSIC_DISC_13,
                    () -> Items.MUSIC_DISC_CAT,
                    () -> Items.MUSIC_DISC_BLOCKS,
                    () -> Items.MUSIC_DISC_CHIRP,
                    () -> Items.MUSIC_DISC_FAR,
                    () -> Items.MUSIC_DISC_MALL,
                    () -> Items.MUSIC_DISC_MELLOHI,
                    () -> Items.MUSIC_DISC_STAL,
                    () -> Items.MUSIC_DISC_STRAD,
                    () -> Items.MUSIC_DISC_WARD,
                    () -> Items.MUSIC_DISC_11,
                    () -> Items.MUSIC_DISC_WAIT);
            this.populateTag(COTWTags.BURN_IMMUNE_WEARABLES, () -> Items.NETHERITE_HELMET, () -> Items.NETHERITE_CHESTPLATE, () -> Items.NETHERITE_LEGGINGS, () -> Items.NETHERITE_BOOTS);
        }

        public void populateTag(TagKey<Item> tag, Supplier<Item>... items){
            for (Supplier<Item> item : items) {
                tag(tag).add(ForgeRegistries.ITEMS.getResourceKey(item.get()).get());
            }
        }
    }

    public static class BlockTagsProvider extends TagsProvider<Block>{

        public BlockTagsProvider(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, Registries.BLOCK, lookupProvider, Constants.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(COTWTags.STONE_TALUS_CAN_DESTROY).addTag(BlockTags.LEAVES).addTag(BlockTags.LOGS);
            this.tag(COTWTags.STONE_TALUS_CAN_CONVERT_TO_DIRT).add(Blocks.GRASS_BLOCK.builtInRegistryHolder().key(), Blocks.PODZOL.builtInRegistryHolder().key(), Blocks.MYCELIUM.builtInRegistryHolder().key());
            this.populateTag(COTWTags.STONE_TALUS_BOULDER_COMPONENT, () -> Blocks.STONE, () -> Blocks.ANDESITE, () -> Blocks.IRON_ORE, () -> Blocks.GOLD_ORE, () -> Blocks.COAL_ORE, () -> Blocks.STONE_SLAB);
            this.populateTag(COTWTags.FROST_TALUS_BOULDER_COMPONENT, () -> Blocks.PACKED_ICE, () -> Blocks.BLUE_ICE);
            this.populateTag(COTWTags.IGNEO_TALUS_BOULDER_COMPONENT, () -> Blocks.MAGMA_BLOCK, () -> Blocks.GOLD_BLOCK, () -> Blocks.ANCIENT_DEBRIS);
        }
        public  <T extends Block>void populateTag(TagKey<Block> tag, Supplier<?>... items){
            for (Supplier<?> item : items) {
                tag(tag).add(ForgeRegistries.BLOCKS.getResourceKey((Block)item.get()).get());
            }
        }
    }

    public static class DamageTypes extends TagsProvider<DamageType>{

        public DamageTypes(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, Registries.DAMAGE_TYPE, lookupProvider, Constants.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(DamageTypeTags.BYPASSES_SHIELD).add(DamageTypeInit.MOB_AREA_OF_EFFECT_ATTACK);
            this.tag(DamageTypeTags.IS_PROJECTILE).add(DamageTypeInit.STONE_TALUS_ARM);
        }
        public void populateTag(TagKey<DamageType> tag, ResourceKey<DamageType>... damageTypes){
            for (ResourceKey<DamageType> damageType : damageTypes) {
                tag(tag).add(damageType);
            }
        }
    }

    public static class COTWBiomeTags extends BiomeTagsProvider {

        public COTWBiomeTags(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, lookupProvider, Constants.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(COTWTags.SPAWNS_STONE_TALUS).add(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.SAVANNA, Biomes.SPARSE_JUNGLE, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.MEADOW);
            this.tag(COTWTags.HAS_STABLES).add(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.SAVANNA, Biomes.SPARSE_JUNGLE);
            this.tag(COTWTags.HAS_STABLES_SNOWY).add(Biomes.SNOWY_PLAINS);


            this.tag(COTWTags.HAS_STONE_TALUS_BOULDERS).add(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.SAVANNA, Biomes.SPARSE_JUNGLE, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.MEADOW);
            this.tag(COTWTags.HAS_FROST_TALUS_BOULDERS).add(Biomes.SNOWY_PLAINS, Biomes.SNOWY_SLOPES, Biomes.GROVE);
            this.tag(COTWTags.HAS_IGNEO_TALUS_BOULDERS).add(Biomes.NETHER_WASTES);
        }
        public void populateTag(TagKey<Biome> tag, ResourceKey<Biome>... damageTypes){
            for (ResourceKey<Biome> damageType : damageTypes) {
                tag(tag).add(damageType);
            }
        }
    }

    public static class EntityTags extends EntityTypeTagsProvider{

        public EntityTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pOutput, pProvider, Constants.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(COTWTags.RADIANT_DISGUISE_AFFECTS).addTag(EntityTypeTags.SKELETONS);
            this.tag(COTWTags.BOKOLBIN_DISGUISE_AFFECTS).add(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.HOGLIN);
            this.tag(COTWTags.STONE_TALUSES).add(EntityInit.STONE_TALUS.get(), EntityInit.FROST_TALUS.get(), EntityInit.IGNEO_TALUS.get(), EntityInit.STONE_TALUS_RARE.get(), EntityInit.STONE_TALUS_LUMINOUS.get());
            this.tag(COTWTags.STONE_PEBBLITS).add(EntityInit.STONE_PEBBLIT.get(), EntityInit.FROST_PEBBLIT.get(), EntityInit.IGNEO_PEBBLIT.get());
            this.tag(COTWTags.STONE_TALUS_FRIENDS).addTags(COTWTags.STONE_TALUSES, COTWTags.STONE_PEBBLITS);
            this.tag(COTWTags.STONE_PEBBLIT_FRIENDS).addTags(COTWTags.STONE_PEBBLITS, COTWTags.STONE_TALUSES);
            this.tag(COTWTags.STONE_TALUS_BOULDER_SPAWNS).add(EntityInit.STONE_TALUS.get(), EntityInit.STONE_TALUS_RARE.get(), EntityInit.STONE_TALUS_LUMINOUS.get());
            this.tag(COTWTags.FROST_TALUS_BOULDER_SPAWNS).add(EntityInit.FROST_TALUS.get());
            this.tag(COTWTags.IGNEO_TALUS_BOULDER_SPAWNS).add(EntityInit.IGNEO_TALUS.get());
            this.tag(COTWTags.STONE_PEBBLIT_BOULDER_SPAWNS).add(EntityInit.STONE_PEBBLIT.get());
            this.tag(COTWTags.FROST_PEBBLIT_BOULDER_SPAWNS).add(EntityInit.FROST_PEBBLIT.get());
            this.tag(COTWTags.IGNEO_PEBBLIT_BOULDER_SPAWNS).add(EntityInit.IGNEO_PEBBLIT.get());
            this.tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(EntityInit.STONE_PEBBLIT.get(), EntityInit.FROST_PEBBLIT.get());
        }
    }

    public static class ModFluidTags extends FluidTagsProvider {

        public ModFluidTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pOutput, pProvider, Constants.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(COTWTags.STONE_PEBBLIT_DROWNS_IN).addTags(FluidTags.WATER, FluidTags.LAVA);
        }
    }
}
