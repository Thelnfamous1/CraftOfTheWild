package com.Thelnfamous1.craft_of_the_wild.item;

import com.Thelnfamous1.craft_of_the_wild.duck.DisguiseEffectUser;
import com.Thelnfamous1.craft_of_the_wild.init.MobEffectInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BokoblinArmorItem extends ArmorItem {
    public static final int MIN_DURATION = COTWUtil.secondsToTicks(10);

    public BokoblinArmorItem(ArmorMaterial $$0, Type $$1, Properties $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public void inventoryTick(ItemStack $$0, Level $$1, Entity $$2, int $$3, boolean $$4) {
        super.inventoryTick($$0, $$1, $$2, $$3, $$4);
        if($$2 instanceof LivingEntity wearer && wearer.getItemBySlot(EquipmentSlot.HEAD) == $$0){
            checkTick(wearer);
        }
    }

    private static void checkTick(LivingEntity wearer) {
        //if (wearer.level().isClientSide()) return;

        if (isFullSetEquipped(wearer) && ((DisguiseEffectUser) wearer).craft_of_the_wild$canApplyDisguiseEffect(MobEffectInit.BOKOBLIN_DISGUISE.get())) {
            if(!wearer.hasEffect(MobEffectInit.BOKOBLIN_DISGUISE.get())){
                wearer.level().broadcastEntityEvent(wearer, EntityEvent.POOF);
            }
            wearer.addEffect(new MobEffectInstance(MobEffectInit.BOKOBLIN_DISGUISE.get(), MIN_DURATION, 0, false, false, true));
        }
    }

    public static boolean isFullSetEquipped(LivingEntity player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BokoblinArmorItem;
    }
}
