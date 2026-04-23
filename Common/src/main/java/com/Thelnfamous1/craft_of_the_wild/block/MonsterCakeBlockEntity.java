package com.Thelnfamous1.craft_of_the_wild.block;

import com.Thelnfamous1.craft_of_the_wild.init.BlockInit;
import com.Thelnfamous1.craft_of_the_wild.recipe.PotionInfusionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MonsterCakeBlockEntity extends BlockEntity {
    private Potion potion = Potions.EMPTY;

    public MonsterCakeBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public MonsterCakeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockInit.MONSTER_CAKE_BE.get(), blockPos, blockState);
    }

    public void applyPotionEffects(Level level, LivingEntity entity){
        PotionInfusionRecipe.applyPotionEffects(level, entity, this.potion);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.potion = PotionInfusionRecipe.getInfusedPotion($$0);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        ResourceLocation resourcelocation = BuiltInRegistries.POTION.getKey(this.potion);
        if (this.potion == Potions.EMPTY) {
            $$0.remove(PotionInfusionRecipe.INFUSED_POTION_TAG_KEY);
        } else {
            $$0.putString(PotionInfusionRecipe.INFUSED_POTION_TAG_KEY, resourcelocation.toString());
        }
    }
}
