package com.Thelnfamous1.craft_of_the_wild.item;

import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.Thelnfamous1.craft_of_the_wild.init.MobEffectInit;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MedalOfHonorTalusItem extends Item implements Equipable {
    public static final int MIN_DURATION = COTWUtil.secondsToTicks(10);
    public MedalOfHonorTalusItem(Properties $$0) {
        super($$0);
    }

    public static boolean isMedalOfHonorTalusEquipped(LivingEntity entity) {
        return entity.getOffhandItem().is(ItemInit.MEDAL_OF_HONOR_TALUS.get()) || Services.PLATFORM.hasCharmEquipped(entity, ItemInit.MEDAL_OF_HONOR_TALUS.get(), "charm");
    }

    public static boolean hasStatusEffect(LivingEntity wearer){
        return wearer.hasEffect(MobEffectInit.MEDAL_OF_HONOR_TALUS.get());
    }

    public static void checkTick(LivingEntity wearer) {
        //if (wearer.level().isClientSide()) return;

        if (isMedalOfHonorTalusEquipped(wearer)) {
            wearer.addEffect(new MobEffectInstance(MobEffectInit.MEDAL_OF_HONOR_TALUS.get(), MIN_DURATION, 0, false, false, true));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        return this.swapWithEquipmentSlot(this, $$0, $$1, $$2);
    }

    /*
    @Override
    public void inventoryTick(ItemStack $$0, Level $$1, Entity $$2, int $$3, boolean $$4) {
        super.inventoryTick($$0, $$1, $$2, $$3, $$4);
        if($$2 instanceof LivingEntity wearer && wearer.getItemBySlot(this.getEquipmentSlot()) == $$0){
            checkTick(wearer);
        }
    }
     */

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.OFFHAND;
    }
}
