package com.Thelnfamous1.craft_of_the_wild.init;

import com.Thelnfamous1.craft_of_the_wild.item.ExtendedReach;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class COTWCustomWeaponItem extends Item implements Vanishable, ExtendedReach {
    protected static final UUID BASE_ATTACK_REACH_UUID = UUID.fromString("aec6e7a3-0e8d-4db4-b319-8d4d36b67dfe");
    public static final UUID BASE_ATTACK_KNOCKBACK_UUID = UUID.fromString("c4f090f4-aa4d-4022-88a5-d011e6c33b40");
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final TagKey<Item> repairItemTag;

    public COTWCustomWeaponItem(Properties $$0, TagKey<Item> repairItemTag, float attackDamage, float attackSpeed) {
        this($$0, repairItemTag, attackDamage, attackSpeed, 3, 0);
    }

    public COTWCustomWeaponItem(Properties $$0, TagKey<Item> repairItemTag, float attackDamage, float attackSpeed, float attackRange) {
        this($$0, repairItemTag, attackDamage, attackSpeed, attackRange, 0);
    }

    public COTWCustomWeaponItem(Properties $$0, TagKey<Item> repairItemTag, float attackDamage, float attackSpeed, float attackReach, float attackKnockback) {
        super($$0);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> $$1 = ImmutableMultimap.builder();
        $$1.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        $$1.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (attackSpeed - 4.0F), AttributeModifier.Operation.ADDITION));
        this.getAttackReachAttribute().ifPresent(attribute -> {
            if(attackReach - 3.0F != 0.0F){
                $$1.put(attribute, new AttributeModifier(BASE_ATTACK_REACH_UUID, "Weapon modifier", (attackReach - 3.0F), AttributeModifier.Operation.ADDITION));
            }
        });
        if(attackKnockback != 0.0F){
            $$1.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_UUID, "Weapon modifier", attackKnockback, AttributeModifier.Operation.ADDITION));
        }
        this.defaultModifiers = $$1.build();
        this.repairItemTag = repairItemTag;
    }

    @Override
    public boolean canAttackBlock(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
        return !$$3.isCreative();
    }

    @Override
    public boolean hurtEnemy(ItemStack $$0, LivingEntity $$1, LivingEntity $$2) {
        $$0.hurtAndBreak(1, $$2, ($$0x) -> {
            $$0x.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack $$0, Level $$1, BlockState $$2, BlockPos $$3, LivingEntity $$4) {
        if ((double)$$2.getDestroySpeed($$1, $$3) != 0.0) {
            $$0.hurtAndBreak(2, $$4, ($$0x) -> {
                $$0x.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot $$0) {
        return $$0 == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers($$0);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack $$0, ItemStack $$1) {
        return $$1.is(this.repairItemTag);
    }
}
