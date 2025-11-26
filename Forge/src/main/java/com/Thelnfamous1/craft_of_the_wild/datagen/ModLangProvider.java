package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.compat.jade.AttackTypeComponentProvider;
import com.Thelnfamous1.craft_of_the_wild.compat.jade.COTWJadePlugin;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusAttackType;
import com.Thelnfamous1.craft_of_the_wild.init.*;
import com.Thelnfamous1.craft_of_the_wild.item.COTWRecordItem;
import com.Thelnfamous1.craft_of_the_wild.util.COTWPaintingVariant;
import com.google.common.collect.ImmutableMap;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModLangProvider extends LanguageProvider {
    protected static final Map<String, String> REPLACE_LIST = ImmutableMap.of(
    );
    protected static final Map<String, String> FULL_REPLACE_LIST = ImmutableMap.of(
            "radiant_helmet", "Radiant Mask",
            "radiant_chestplate", "Radiant Shirt",
            "radiant_leggings", "Radiant Tights",
            "bokolbin_helmet", "Bokolbin Mask",
            "medal_of_honor_talus", "Medal of Honor: Talus"
    );

    public ModLangProvider(PackOutput gen) {
        super(gen, Constants.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ItemInit.ITEMS.getEntries().forEach(this::itemLang);
        EntityInit.ENTITIES.getEntries().forEach(this::entityLang);
        BlockInit.BLOCKS.getEntries().forEach(this::blockLang);
        AttributeInit.ATTRIBUTES.getEntries().forEach(this::attributeLang);
        MobEffectInit.MOB_EFFECTS.getEntries().forEach(this::mobEffectLang);
        add("itemGroup." + Constants.MODID + ".tab", Constants.MOD_NAME);
        add(COTWJadePlugin.getConfigTranslationKey(COTWJadePlugin.ATTACK_TYPE), "Attack Type");
        add(AttackTypeComponentProvider.ATTACK_TYPE_TRANSLATION_KEY, "Attack Type");
        for(StoneTalusAttackType attackType : StoneTalusAttackType.values()){
            add(AttackTypeComponentProvider.getAttackTypeDisplayName(EntityInit.STONE_TALUS.get(), attackType).getString(), checkReplace(attackType.getKey()));
        }
        this.projectileDamageTypeLang(DamageTypeInit.STONE_TALUS_ARM, "a stony arm");
        add(ModAdventureAdvancements.ADVANCEMENTS_ROOT_TITLE, "A New Adventure");
        add(ModAdventureAdvancements.ADVANCEMENTS_ROOT_DESCRIPTION, "Discover a Stable");
        add(ModAdventureAdvancements.KASS_QUEST_START_TITLE, "A Feathery Bard");
        add(ModAdventureAdvancements.KASS_QUEST_START_DESCRIPTION, "Give a Music Disc to Kass");
        add(ModAdventureAdvancements.KASS_QUEST_FINISH_TITLE, "The Lost Song");
        add(ModAdventureAdvancements.KASS_QUEST_FINISH_DESCRIPTION, "Give Kass all Music Discs in a Meadows biome");
        add(ModAdventureAdvancements.DEFEAT_STONE_TALUS_TITLE, "Rock Hunter");
        add(ModAdventureAdvancements.DEFEAT_STONE_TALUS_DESCRIPTION, "Defeat a Stone Talus");
        add(ModAdventureAdvancements.DEFEAT_TWENTY_STONE_TALUS_TITLE, "WANTED: Stone Talus");
        add(ModAdventureAdvancements.DEFEAT_TWENTY_STONE_TALUS_DESCRIPTION, "Defeat 20 Stone Talus");
        PaintingVariantInit.PAINTING_VARIANTS.getEntries().forEach(paintingVariantRegistryObject -> {
            ResourceLocation id = paintingVariantRegistryObject.getId();
            this.add(id.toLanguageKey("painting", "title"), checkReplace(id));
            this.add(id.toLanguageKey("painting", "author"),
                    paintingVariantRegistryObject.get() instanceof COTWPaintingVariant paintingVariant ? paintingVariant.getAuthor() : "Unknown");
        });
    }

    private void projectileDamageTypeLang(ResourceKey<DamageType> resourceKey, @Nullable String projectileDescription) {
        ResourceLocation location = resourceKey.location();
        String midSentence = projectileDescription == null ? " " : " %s from ".formatted(projectileDescription); // either just " ", or " <projectile name> from "
        this.add("death.attack.%s".formatted(location.toLanguageKey()), "%1$s was shot by" + midSentence + "%2$s");
        this.add("death.attack.%s.item".formatted(location.toLanguageKey()), "%1$s was shot by" + midSentence + "%2$s using %3$s");
    }

    protected void attributeLang(RegistryObject<Attribute> entry) {
        String[] splitPath = entry.getId().getPath().split("\\.");
        for(int i = 0; i < splitPath.length; i++){
            splitPath[i] = checkReplaceString(splitPath[i]);
            if(splitPath[i].equals("Generic")){
                splitPath[i] = "";
            }
        }
        this.add(entry.get().getDescriptionId(), this.checkReplace(Arrays.stream(splitPath)));
    }

    protected void itemLang(RegistryObject<Item> entry) {
        if (!(entry.get() instanceof BlockItem) || entry.get() instanceof ItemNameBlockItem) {
            if(entry.get() instanceof COTWRecordItem recordItem){
                // "music_disc" = 10 characters
                String name = entry.getId().getPath().substring(0, 10);
                String desc = entry.getId().getPath().substring(11);
                addItem(entry, checkReplace(Arrays.stream(name.split("_"))));
                add(entry.get().getDescriptionId() + ".desc", recordItem.getAuthor() + " - " + checkReplace(Arrays.stream(desc.split("_"))));
            } else{
                addItem(entry, checkReplace(entry));
            }
        }
    }

    protected void blockLang(RegistryObject<Block> entry) {
        addBlock(entry, checkReplace(entry));
    }

    protected void entityLang(RegistryObject<EntityType<?>> entry) {
        addEntityType(entry, checkReplace(entry));
    }

    protected void mobEffectLang(RegistryObject<MobEffect> entry) {
        addEffect(entry, checkReplace(entry));
    }

    protected String checkReplace(ResourceKey<?> registryObject) {
        return this.checkReplace(registryObject.location());
    }

    protected String checkReplace(RegistryObject<?> registryObject) {
        return this.checkReplace(registryObject.getId());
    }

    protected String checkReplace(ResourceLocation location) {
        String path = location.getPath();
        if(FULL_REPLACE_LIST.containsKey(path)){
            return FULL_REPLACE_LIST.get(path);
        }
        return checkReplace(Arrays.stream(path.split("_")));
    }

    private String checkReplace(Stream<String> stream) {
        return stream
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected String checkReplaceString(String string) {
        return checkReplace(Arrays.stream(string.split("_")));
    }

    protected String checkReplace(String string) {
        return REPLACE_LIST.containsKey(string) ? REPLACE_LIST.get(string) : StringUtils.capitalize(string);
    }
}
