package com.Thelnfamous1.craft_of_the_wild.recipe;

import com.Thelnfamous1.craft_of_the_wild.init.RecipeSerializerInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.google.common.collect.Lists;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class PotionInfusionRecipe extends CustomRecipe {
    public static final String INFUSED_POTION_TAG_KEY = "InfusedPotion";
    public static final String POTION_EFFECT_TRANSLATION_KEY = "potion";

    public PotionInfusionRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    public static void applyPotionEffects(Level level, LivingEntity entity, Potion potion) {
        if (!level.isClientSide) {
            List<MobEffectInstance> potionEffects = potion.getEffects();

            for (MobEffectInstance effectInstance : potionEffects) {
                if (effectInstance.getEffect().isInstantenous()) {
                    effectInstance.getEffect().applyInstantenousEffect(entity, entity, entity, effectInstance.getAmplifier(), 1.0);
                } else {
                    entity.addEffect(new MobEffectInstance(effectInstance));
                }
            }
        }
    }

    public static String extendDescriptionIdWithInfused(ItemStack $$0) {
        return $$0.getItem().getDescriptionId($$0) + ".infused";
    }

    public static String getPotionDescriptionId(Potion infusedPotion) {
        return infusedPotion.getName(String.format("%s.%s.", POTION_EFFECT_TRANSLATION_KEY, BuiltInRegistries.POTION.getKey(infusedPotion).getNamespace()));
    }

    @Override
    public boolean matches(CraftingContainer container, Level $$1) {
        ItemStack infusable = ItemStack.EMPTY;
        ItemStack potion = ItemStack.EMPTY;

        for(int $$4 = 0; $$4 < container.getContainerSize(); ++$$4) {
            ItemStack stackInSlot = container.getItem($$4);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.is(COTWTags.POTION_INFUSABLE)) {
                    if (!infusable.isEmpty()) {
                        return false;
                    }
                    if(hasInfusedPotion(infusable)){
                        return false;
                    }

                    infusable = stackInSlot;
                } else {
                    if (!isPotion(stackInSlot)) {
                        return false;
                    }
                    if (!potion.isEmpty()) {
                        return false;
                    }

                    potion = stackInSlot;
                }
            }
        }

        return !infusable.isEmpty() && !potion.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess $$1) {
        ItemStack infusable = ItemStack.EMPTY;
        ItemStack potion = ItemStack.EMPTY;

        for(int $$4 = 0; $$4 < container.getContainerSize(); ++$$4) {
            ItemStack stackInSlot = container.getItem($$4);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.is(COTWTags.POTION_INFUSABLE)) {
                    if (!infusable.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if(hasInfusedPotion(infusable)){
                        return ItemStack.EMPTY;
                    }

                    infusable = stackInSlot.copy();
                } else {
                    if (!isPotion(stackInSlot)) {
                        if (!infusable.isEmpty()) {
                            return ItemStack.EMPTY;
                        }
                        if(potion.isEmpty()){
                            return ItemStack.EMPTY;
                        }
                    }

                    potion = stackInSlot.copy();
                }
            }
        }

        if (!infusable.isEmpty() && !potion.isEmpty()) {
            return infusePotion(infusable, potion);
        } else {
            return ItemStack.EMPTY;
        }
    }

    private static ItemStack infusePotion(ItemStack infusable, ItemStack potion) {
        return setInfusedPotion(infusable, PotionUtils.getPotion(potion));
    }

    public static ItemStack setInfusedPotion(ItemStack $$0, Potion $$1) {
        ResourceLocation $$2 = BuiltInRegistries.POTION.getKey($$1);
        if ($$1 == Potions.EMPTY) {
            $$0.removeTagKey(INFUSED_POTION_TAG_KEY);
        } else {
            $$0.getOrCreateTag().putString(INFUSED_POTION_TAG_KEY, $$2.toString());
        }

        return $$0;
    }

    public static CompoundTag setInfusedPotion(CompoundTag $$0, Potion $$1) {
        ResourceLocation $$2 = BuiltInRegistries.POTION.getKey($$1);
        if ($$1 == Potions.EMPTY) {
            $$0.remove(INFUSED_POTION_TAG_KEY);
        } else {
            $$0.putString(INFUSED_POTION_TAG_KEY, $$2.toString());
        }

        return $$0;
    }

    public static boolean hasInfusedPotion(ItemStack stackInSlot) {
        return stackInSlot.hasTag() && hasInfusedPotion(stackInSlot.getTag());
    }

    public static boolean hasInfusedPotion(CompoundTag tag){
        return tag.contains(INFUSED_POTION_TAG_KEY, Tag.TAG_STRING);
    }

    private static boolean isPotion(ItemStack stackInSlot) {
        return stackInSlot.hasTag() && stackInSlot.getTag().contains(PotionUtils.TAG_POTION, Tag.TAG_STRING);
    }

    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerInit.POTION_INFUSION.get();
    }

    public static Potion getInfusedPotion(ItemStack $$0) {
        return getInfusedPotion($$0.getTag());
    }

    public static Potion getInfusedPotion(@Nullable CompoundTag $$0) {
        return $$0 == null ? Potions.EMPTY : Potion.byName($$0.getString(INFUSED_POTION_TAG_KEY));
    }

    public static void addInfusedPotionTooltip(ItemStack $$0, List<Component> $$1, float $$2) {
        PotionUtils.addPotionTooltip(getInfusedMobEffects($$0), $$1, $$2);
    }

    public static List<MobEffectInstance> getInfusedMobEffects(ItemStack $$0) {
        return getAllInfusedEffects($$0.getTag());
    }

    public static List<MobEffectInstance> getAllInfusedEffects(Potion $$0, Collection<MobEffectInstance> $$1) {
        List<MobEffectInstance> $$2 = Lists.newArrayList();
        $$2.addAll($$0.getEffects());
        $$2.addAll($$1);
        return $$2;
    }

    public static List<MobEffectInstance> getAllInfusedEffects(@Nullable CompoundTag $$0) {
        List<MobEffectInstance> $$1 = Lists.newArrayList();
        $$1.addAll(getInfusedPotion($$0).getEffects());
        PotionUtils.getCustomEffects($$0, $$1);
        return $$1;
    }
}