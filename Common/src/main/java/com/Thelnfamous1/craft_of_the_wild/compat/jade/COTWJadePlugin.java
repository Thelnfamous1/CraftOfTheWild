package com.Thelnfamous1.craft_of_the_wild.compat.jade;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.entity.COTWMonster;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class COTWJadePlugin implements IWailaPlugin {
    public static final ResourceLocation ATTACK_TYPE = COTWCommon.getResourceLocation("attack_type");

    public static String getConfigTranslationKey(ResourceLocation location){
        return "config.jade.plugin_%s.%s".formatted(location.getNamespace(), location.getPath());
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
    }


    @Override
    public void registerClient(IWailaClientRegistration registration) {
      registration.registerEntityComponent(AttackTypeComponentProvider.INSTANCE, COTWMonster.class);
    }
}