package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.client.COTWCommonClient;
import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.item.COTWSpawnEggItem;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, Constants.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Stream.of()
        //         .map(Supplier::get)
        //         .forEach(this::simpleHandHeldModel);

        // Stream.of()
        //         .map(Supplier::get)
        //         .forEach(this::simpleGeneratedModel);

        // Stream.of()
        //         .map(Supplier::get)
        //         .forEach(this::simpleBlockItemModel);
        COTWSpawnEggItem.getEggs().forEach(this::spawnEgg);
        this.simpleGeneratedModel(ItemInit.MUSIC_DISC_A_ROCKY_BALLAD.get());
        this.simpleGeneratedModel(ItemInit.MUSIC_DISC_LOST_CITY.get());
        this.simpleGeneratedModel(ItemInit.MUSIC_DISC_OF_THE_WILD.get());
        this.simpleGeneratedModel(ItemInit.MUSIC_DISC_STABLES.get());
        this.simpleGeneratedModel(ItemInit.EGG_PUDDING.get());


        ItemModelBuilder unlitLuminousStone = this.simpleGeneratedModel(ItemInit.LUMINOUS_STONE.get());
        for(int i = 1; i <= 15; i++){
            ItemModelBuilder glowingFrame = this.simpleGeneratedModel(ItemInit.LUMINOUS_STONE.getId().withSuffix("_glowing" + String.format(Locale.ROOT, "_%02d", i)));
            unlitLuminousStone
                    .override()
                    .predicate(COTWCommonClient.GLOW_ITEM_PROPERTY, i / 15.0F)
                    .model(glowingFrame)
                    .end();
        }
        this.simpleGeneratedModel(ItemInit.RADIANT_HELMET.get());
        this.simpleGeneratedModel(ItemInit.RADIANT_CHESTPLATE.get()).texture("layer1", ItemInit.RADIANT_CHESTPLATE.getId().withPrefix("item/").withSuffix("_overlay"));
        this.simpleGeneratedModel(ItemInit.RADIANT_LEGGINGS.get());
        this.simpleGeneratedModel(ItemInit.RADIANT_BOOTS.get());
        this.simpleGeneratedModel(ItemInit.MEDAL_OF_HONOR_TALUS.get());
        this.simpleGeneratedModel(ItemInit.MON.get());
        this.simpleGeneratedModel(ItemInit.BOKOBLIN_HELMET.get());
        this.simpleGeneratedModel(ItemInit.TRAVELERS_SADDLE.get());
        this.simpleGeneratedModel(ItemInit.MONSTER_HORSE_ARMOR.get());
        ItemModelBuilder hylianHelmetHoodDown = this.simpleGeneratedModel(ItemInit.HYLIAN_HELMET.getId().withSuffix("_alternate"));
        this.simpleGeneratedModel(ItemInit.HYLIAN_HELMET.get())
                .override()
                .predicate(COTWCommonClient.USE_ALTERNATE_PROPERTY, 1.0F)
                .model(hylianHelmetHoodDown)
                .end();
        this.simpleGeneratedModel(ItemInit.HYLIAN_CHESTPLATE.get());
        this.simpleGeneratedModel(ItemInit.HYLIAN_LEGGINGS.get());
        this.simpleGeneratedModel(ItemInit.HYLIAN_BOOTS.get());
        this.simpleGeneratedModel(ItemInit.MONSTER_EXTRACT.get());
        this.simpleGeneratedModel(ItemInit.MONSTER_STEW.get());
        this.simpleGeneratedModel(ItemInit.MONSTER_CAKE.get());

        this.simpleHandHeldModel(ItemInit.SPRING_LOADED_HAMMER.get());
        ItemModelBuilder woodenMopGui = simpleModel(ItemInit.WOODEN_MOP.getId().withSuffix("_gui").getPath(), mcLoc("item/generated"));
        ItemModelBuilder woodenMopHandheld = simpleModel(ItemInit.WOODEN_MOP.getId().withSuffix("_handheld").getPath(), mcLoc("item/handheld")).transforms()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0, -90, 55).translation(0, 4.0F, 0.5F).scale(1.7F, 1.7F, 0.85F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(0, 90, -55).translation(0, 4.0F, 0.5F).scale(1.7F, 1.7F, 0.85F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, -90, 25).translation(1.13F, 3.2F, 1.13F).scale(1.36F, 1.36F, 0.68F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 90, -25).translation(1.13F, 3.2F, 1.13F).scale(1.36F, 1.36F, 0.68F).end()
                .end();
        ItemModelBuilder dummyWMG = new ItemModelBuilder(woodenMopGui.getLocation(), this.existingFileHelper).parent(new ModelFile.UncheckedModelFile(woodenMopGui.getLocation()));
        ItemModelBuilder dummyWMH = new ItemModelBuilder(woodenMopGui.getLocation(), this.existingFileHelper).parent(new ModelFile.UncheckedModelFile(woodenMopHandheld.getLocation()));
        this.withExistingParent(ItemInit.WOODEN_MOP.getId().getPath(), mcLoc("item/generated"))
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(dummyWMG)
                .perspective(ItemDisplayContext.GUI,
                        dummyWMG)
                .perspective(ItemDisplayContext.GROUND,
                        dummyWMG)
                .perspective(ItemDisplayContext.FIXED,
                        dummyWMG)
                .perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                        dummyWMH)
                .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                        dummyWMH)
                .perspective(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                        dummyWMH)
                .perspective(ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                        dummyWMH)
                .end();
    }

    protected ItemModelBuilder spawnEgg(Item item) {
        return withExistingParent(getName(item), mcLoc("item/template_spawn_egg"));
    }

    protected ItemModelBuilder simpleBlockItemModel(Block block) {
        String name = getName(block);
        return withExistingParent(name, modLoc("block/" + name));
    }

    protected ItemModelBuilder simpleGeneratedModel(Item item) {
        return simpleModel(item, mcLoc("item/generated"));
    }

    protected ItemModelBuilder simpleHandHeldModel(Item item) {
        return simpleModel(item, mcLoc("item/handheld"));
    }

    protected ItemModelBuilder simpleModel(Item item, ResourceLocation parent) {
        String name = getName(item);
        return simpleModel(name, parent);
    }

    protected ItemModelBuilder simpleGeneratedModel(ResourceLocation item) {
        return simpleModel(getName(item), mcLoc("item/generated"));
    }

    protected ItemModelBuilder simpleModel(String name, ResourceLocation parent) {
        return singleTexture(name, parent, "layer0", modLoc("item/" + name));
    }

    protected String getName(Item item) {
        return getName(ForgeRegistries.ITEMS.getKey(item));
    }

    protected String getName(Block item) {
        return getName(ForgeRegistries.BLOCKS.getKey(item));
    }

    protected String getName(ResourceLocation item) {
        return item.getPath();
    }
}
