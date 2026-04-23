package com.Thelnfamous1.craft_of_the_wild.mixin.client;

import com.Thelnfamous1.craft_of_the_wild.init.ItemInit;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getCloakTextureLocation()Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation wrap_getCloakTextureLocation_render(AbstractClientPlayer instance, Operation<ResourceLocation> original){
        if(instance.getItemBySlot(EquipmentSlot.HEAD).is(ItemInit.HYLIAN_HELMET.get())){
            return ItemInit.HYLIAN_CAPE_TEXTURE_LOCATION;
        }

        return original.call(instance);
    }
}
