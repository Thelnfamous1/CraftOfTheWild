package com.Thelnfamous1.craft_of_the_wild.recipe;

import com.Thelnfamous1.craft_of_the_wild.init.RecipeSerializerInit;
import com.Thelnfamous1.craft_of_the_wild.item.AlternateArmorItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AlternateArmorRecipe extends CustomRecipe {
   public AlternateArmorRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
      super(pId, pCategory);
   }

   @Override
   public boolean matches(CraftingContainer pInv, Level pLevel) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < pInv.getContainerSize(); ++i) {
         ItemStack stackInSlot = pInv.getItem(i);
         if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getItem() instanceof AlternateArmorItem) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = stackInSlot;
            } else {
               return false;
            }
         }
      }

      return !itemstack.isEmpty();
   }

   @Override
   public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < pContainer.getContainerSize(); ++i) {
         ItemStack stackInSlot = pContainer.getItem(i);
         if (!stackInSlot.isEmpty()) {
            Item item = stackInSlot.getItem();
            if (item instanceof AlternateArmorItem) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = stackInSlot.copy();
               AlternateArmorItem.setUseAlternate(itemstack, !AlternateArmorItem.usingAlternate(stackInSlot));
            } else {
               return ItemStack.EMPTY;
            }
         }
      }

      return itemstack;
   }

   @Override
   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return pWidth * pHeight >= 2;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializerInit.ALTERNATE_ARMOR.get();
   }
}