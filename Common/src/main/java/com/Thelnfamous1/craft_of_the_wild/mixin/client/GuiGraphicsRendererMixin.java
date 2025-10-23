package com.Thelnfamous1.craft_of_the_wild.mixin.client;

import com.Thelnfamous1.craft_of_the_wild.client.renderer.EntityAwareItemRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphics.class)
public class GuiGraphicsRendererMixin {

    @WrapOperation(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
    private void wrap_render_renderItem(ItemRenderer instance, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model, Operation<Void> original,
                                        @Nullable LivingEntity $$0, @Nullable Level $$1, ItemStack $$2, int $$3, int $$4, int $$5, int $$6){
        ((EntityAwareItemRenderer)instance).craft_of_the_wild$setCurrentEntity($$0);
        original.call(instance, itemStack, displayContext, leftHand, poseStack, buffer, combinedLight, combinedOverlay, model);
        ((EntityAwareItemRenderer)instance).craft_of_the_wild$setCurrentEntity(null);
    }
}
