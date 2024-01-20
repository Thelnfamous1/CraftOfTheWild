package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.compat.jade.AttackTypeComponentProvider;
import com.Thelnfamous1.craft_of_the_wild.compat.jade.COTWJadePlugin;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalusAttackType;
import com.Thelnfamous1.craft_of_the_wild.init.BlockInit;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.google.common.collect.ImmutableMap;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ModLangProvider extends LanguageProvider {
    protected static final Map<String, String> REPLACE_LIST = ImmutableMap.of(
            "tnt", "TNT",
            "sus", ""
    );

    public ModLangProvider(PackOutput gen) {
        super(gen, Constants.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ItemInit.ITEMS.getEntries().forEach(this::itemLang);
        EntityInit.ENTITIES.getEntries().forEach(this::entityLang);
        BlockInit.BLOCKS.getEntries().forEach(this::blockLang);
        add("itemGroup." + Constants.MODID + ".tab", Constants.MOD_NAME);
        add(COTWJadePlugin.getConfigTranslationKey(COTWJadePlugin.ATTACK_TYPE), "Attack Type");
        add(AttackTypeComponentProvider.ATTACK_TYPE_TRANSLATION_KEY, "Attack Type");
        for(StoneTalusAttackType attackType : StoneTalusAttackType.values()){
            add(AttackTypeComponentProvider.getAttackTypeDisplayName(EntityInit.STONE_TALUS.get(), attackType).getString(), checkReplace(attackType.getKey()));
        }
    }

    protected void itemLang(RegistryObject<Item> entry) {
        if (!(entry.get() instanceof BlockItem) || entry.get() instanceof ItemNameBlockItem) {
            addItem(entry, checkReplace(entry));
        }
    }

    protected void blockLang(RegistryObject<Block> entry) {
        addBlock(entry, checkReplace(entry));
    }

    protected void entityLang(RegistryObject<EntityType<?>> entry) {
        addEntityType(entry, checkReplace(entry));
    }

    protected String checkReplace(ResourceKey<?> registryObject) {
        return this.checkReplace(registryObject.location());
    }

    protected String checkReplace(RegistryObject<?> registryObject) {
        return this.checkReplace(registryObject.getId());
    }

    protected String checkReplace(ResourceLocation location) {
        return Arrays.stream(location.getPath().split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected String checkReplaceString(String string) {
        return Arrays.stream(string.split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected String checkReplace(String string) {
        return REPLACE_LIST.containsKey(string) ? REPLACE_LIST.get(string) : StringUtils.capitalize(string);
    }
}
