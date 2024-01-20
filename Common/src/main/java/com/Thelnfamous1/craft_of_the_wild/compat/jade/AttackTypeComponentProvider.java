package com.Thelnfamous1.craft_of_the_wild.compat.jade;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.entity.AnimatedAttacker;
import com.Thelnfamous1.craft_of_the_wild.entity.COTWMonster;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum AttackTypeComponentProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final String ATTACK_TYPE_TRANSLATION_KEY = Util.makeDescriptionId("options", COTWCommon.getResourceLocation("attack_type"));

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        COTWMonster<?> monster = (COTWMonster<?>) entityAccessor.getEntity();
        AnimatedAttacker.AttackType currentAttackType = monster.getCurrentAttackType();
        if(currentAttackType != null){
            iTooltip.add(CommonComponents.optionNameValue(Component.translatable(ATTACK_TYPE_TRANSLATION_KEY), getAttackTypeDisplayName(monster.getType(), currentAttackType)));
        }
    }

    public static MutableComponent getAttackTypeDisplayName(EntityType<?> type, AnimatedAttacker.AttackType currentAttackType) {
        return Component.translatable("%s.attack_type.%s".formatted(type.getDescriptionId(), currentAttackType.getKey()));
    }

    @Override
    public ResourceLocation getUid() {
        return COTWJadePlugin.ATTACK_TYPE;
    }
}
