package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.item.MedalOfHonorTalusItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BonusLevelTableCondition.class)
public class BonusLevelTableConditionMixin {

    @Shadow @Final
    Enchantment enchantment;

    @ModifyVariable(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private int modify_efficiencyLevel_getDestroySpeed(int original, LootContext context){
        LivingEntity entity = context.hasParam(LootContextParams.THIS_ENTITY) ?
                context.getParam(LootContextParams.THIS_ENTITY) instanceof LivingEntity living ? living : null : null;
        if(this.enchantment == Enchantments.BLOCK_FORTUNE && entity != null && MedalOfHonorTalusItem.hasStatusEffect(entity)){
            original += 2;
        }
        return original;
    }
}
