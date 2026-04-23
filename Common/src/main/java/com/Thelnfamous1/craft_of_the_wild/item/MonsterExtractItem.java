package com.Thelnfamous1.craft_of_the_wild.item;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MonsterExtractItem extends Item {
    private static final int DRINK_DURATION = 40;
    public static final int EFFECT_DURATION = 200;

    public MonsterExtractItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
        super.finishUsingItem($$0, $$1, $$2);
        if ($$2 instanceof ServerPlayer $$3) {
            CriteriaTriggers.CONSUME_ITEM.trigger($$3, $$0);
            $$3.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!$$1.isClientSide) {
            ArrayList<MobEffect> harmfulPotionEffects = BuiltInRegistries.MOB_EFFECT.stream().filter(mobEffect -> mobEffect.getCategory() == MobEffectCategory.HARMFUL).collect(Collectors.toCollection(ArrayList::new));
            Util.getRandomSafe(harmfulPotionEffects, $$2.getRandom()).ifPresent(effect -> $$2.addEffect(new MobEffectInstance(effect, EFFECT_DURATION)));
        }
        return $$0;
    }

    @Override
    public int getUseDuration(ItemStack $$0) {
        return DRINK_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack $$0) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        return ItemUtils.startUsingInstantly($$0, $$1, $$2);
    }
}