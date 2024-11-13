package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.init.DamageTypeInit;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
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
        }

        public void populateTag(TagKey<Item> tag, Supplier<Item>... items){
            for (Supplier<Item> item : items) {
                tag(tag).add(ForgeRegistries.ITEMS.getResourceKey(item.get()).get());
            }
        }
    }

    public static class Blocks extends TagsProvider<Block>{

        public Blocks(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, Registries.BLOCK, lookupProvider, Constants.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(COTWTags.STONE_TALUS_CAN_DESTROY).addTag(BlockTags.LEAVES).addTag(BlockTags.LOGS);
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
            this.tag(COTWTags.SPAWNS_STONE_TALUS).add(Biomes.PLAINS, Biomes.MEADOW);
        }
        public void populateTag(TagKey<Biome> tag, ResourceKey<Biome>... damageTypes){
            for (ResourceKey<Biome> damageType : damageTypes) {
                tag(tag).add(damageType);
            }
        }
    }
}
