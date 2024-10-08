package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import java.util.function.Predicate;

public abstract class COTWMonster<T extends AnimatedAttacker.AttackType> extends COTWAttacker<T> implements Enemy {

    public COTWMonster(EntityType<? extends COTWMonster> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    // Monster-related methods

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        this.updateNoActionTime();
        super.aiStep();
    }

    protected void updateNoActionTime() {
        float value = this.getLightLevelDependentMagicValue();
        if (value > 0.5F) {
            this.noActionTime += 2;
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.HOSTILE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOSTILE_DEATH;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.HOSTILE_SMALL_FALL, SoundEvents.HOSTILE_BIG_FALL);
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return -pLevel.getPathfindingCostFromLightLevels(pPos);
    }

    @Override
    public boolean shouldDropExperience() {
        return true;
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }

    public boolean isPreventingPlayerRest(Player player) {
        return true;
    }

    @Override
    public ItemStack getProjectile(ItemStack $$0) {
        if ($$0.getItem() instanceof ProjectileWeaponItem) {
            Predicate<ItemStack> $$1 = ((ProjectileWeaponItem)$$0.getItem()).getSupportedHeldProjectiles();
            ItemStack $$2 = ProjectileWeaponItem.getHeldProjectile(this, $$1);
            return $$2.isEmpty() ? new ItemStack(Items.ARROW) : $$2;
        } else {
            return ItemStack.EMPTY;
        }
    }
}
