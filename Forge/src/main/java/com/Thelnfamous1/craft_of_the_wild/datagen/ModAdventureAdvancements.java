package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWLootTables;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class ModAdventureAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {

    public static final String ADVANCEMENTS_ROOT_TITLE = COTWCommon.makeDescriptionId("advancements", "adventure/root/title");
    public static final String ADVANCEMENTS_ROOT_DESCRIPTION = COTWCommon.makeDescriptionId("advancements", "adventure/root/description");
    public static final String KASS_QUEST_START_TITLE = COTWCommon.makeDescriptionId("advancements", "adventure/kass_quest_start/title");
    public static final String KASS_QUEST_START_DESCRIPTION = COTWCommon.makeDescriptionId("advancements", "adventure/kass_quest_start/description");
    public static final String KASS_QUEST_FINISH_TITLE = COTWCommon.makeDescriptionId("advancements", "adventure/kass_quest_finish/title");
    public static final String KASS_QUEST_FINISH_DESCRIPTION = COTWCommon.makeDescriptionId("advancements", "adventure/kass_quest_finish/description");

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        Advancement root = Advancement.Builder.advancement()
                .display(
                        Items.MAP,
                        Component.translatable(ADVANCEMENTS_ROOT_TITLE),
                        Component.translatable(ADVANCEMENTS_ROOT_DESCRIPTION),
                        new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false)
                .requirements(RequirementsStrategy.OR)
                .addCriterion("killed_something",
                        KilledTrigger.TriggerInstance.playerKilledEntity())
                .addCriterion("killed_by_something",
                        KilledTrigger.TriggerInstance.entityKilledPlayer())
                .save(saver, COTWCommon.getResourceLocation("adventure/root"), existingFileHelper);
        Advancement kassQuestStart = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ItemInit.MUSIC_DISC_STABLES.get(),
                        Component.translatable(KASS_QUEST_START_TITLE),
                        Component.translatable(KASS_QUEST_START_DESCRIPTION),
                        (ResourceLocation)null, FrameType.TASK, true, true, false)
                .addCriterion("kass_play", kassMusicTrigger(ItemTags.MUSIC_DISCS))
                .save(saver, COTWCommon.getResourceLocation("adventure/kass_quest_start"), existingFileHelper);
        Advancement.Builder.advancement()
                .parent(kassQuestStart)
                .display(
                        ItemInit.MUSIC_DISC_OF_THE_WILD.get(),
                        Component.translatable(KASS_QUEST_FINISH_TITLE),
                        Component.translatable(KASS_QUEST_FINISH_DESCRIPTION),
                        (ResourceLocation)null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.AND)
                .addCriterion("kass_play_c418", kassMusicTriggerInBiome(COTWTags.C418_MUSIC_DISCS))
                .addCriterion("kass_play_otherside", kassMusicTriggerInBiome(Items.MUSIC_DISC_OTHERSIDE))
                .addCriterion("kass_play_pigstep", kassMusicTriggerInBiome(Items.MUSIC_DISC_PIGSTEP))
                .addCriterion("kass_play_5", kassMusicTriggerInBiome(Items.MUSIC_DISC_5))
                .addCriterion("kass_play_stables", kassMusicTriggerInBiome(ItemInit.MUSIC_DISC_STABLES.get()))
                .addCriterion("kass_play_lost_city", kassMusicTriggerInBiome(ItemInit.MUSIC_DISC_LOST_CITY.get()))
                .addCriterion("kass_play_a_rocky_ballad", kassMusicTriggerInBiome(ItemInit.MUSIC_DISC_A_ROCKY_BALLAD.get()))
                .rewards(AdvancementRewards.Builder.loot(COTWLootTables.KASS_QUEST_FINISH))
                .save(saver, COTWCommon.getResourceLocation("adventure/kass_quest_finish"), existingFileHelper);

    }

    private static PlayerInteractTrigger.TriggerInstance kassMusicTrigger(TagKey<Item> musicDiscs) {
        return PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                ItemPredicate.Builder.item().of(musicDiscs),
                EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityInit.KASS.get()).build()));
    }

    private static PlayerInteractTrigger.TriggerInstance kassMusicTriggerInBiome(TagKey<Item> musicDiscs) {
        return PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                ItemPredicate.Builder.item().of(musicDiscs),
                EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityInit.KASS.get()).located(LocationPredicate.inBiome(Biomes.MEADOW)).build()));
    }

    private static PlayerInteractTrigger.TriggerInstance kassMusicTriggerInBiome(Item musicDisc) {
        return PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                ItemPredicate.Builder.item().of(musicDisc),
                EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityInit.KASS.get()).located(LocationPredicate.inBiome(Biomes.MEADOW)).build()));
    }
}
