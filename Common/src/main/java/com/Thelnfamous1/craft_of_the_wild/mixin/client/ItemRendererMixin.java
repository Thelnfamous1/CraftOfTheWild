package com.Thelnfamous1.craft_of_the_wild.mixin.client;

import com.Thelnfamous1.craft_of_the_wild.duck.EntityAwareItemRenderer;
import com.Thelnfamous1.craft_of_the_wild.item.RadiantItem;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin implements EntityAwareItemRenderer{

    @Unique
    @Nullable
    private LivingEntity craft_of_the_wild$currentEntity;

    @Inject(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
    private void pre_render_renderStatic(LivingEntity $$0, ItemStack $$1, ItemDisplayContext $$2, boolean $$3, PoseStack $$4, MultiBufferSource $$5, Level $$6, int $$7, int $$8, int $$9, CallbackInfo ci){
        this.craft_of_the_wild$setCurrentEntity($$0);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    private void wrap_renderModelLists_render(ItemRenderer instance, BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack matrixStack, VertexConsumer buffer, Operation<Void> original){
        if(stack.getItem() instanceof RadiantItem){
            LivingEntity currentEntity = this.craft_of_the_wild$getCurrentEntity();
            Entity contextEntity = currentEntity != null ? currentEntity : stack.getEntityRepresentation();
            Level level = contextEntity != null ? contextEntity.level() : Minecraft.getInstance().level;
            BlockPos pos = contextEntity != null ? contextEntity.blockPosition() : BlockPos.ZERO;

            float darkness = COTWUtil.getLocalDarknessFactor(0.0F, level, pos);

            // Use full-bright if dark enough
            combinedLight = darkness >= 0.0625F ? COTWUtil.invertPackedLightByDarkness(combinedLight, darkness) : combinedLight;
        }
        original.call(instance, model, stack, combinedLight, combinedOverlay, matrixStack, buffer);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;renderByItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"))
    private void wrap_renderByItem_render(BlockEntityWithoutLevelRenderer instance, ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, Operation<Void> original){
        if(instance instanceof EntityAwareItemRenderer eair){
            eair.craft_of_the_wild$setCurrentEntity(this.craft_of_the_wild$getCurrentEntity());
        }
        original.call(instance, stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        if(instance instanceof EntityAwareItemRenderer eair){
            eair.craft_of_the_wild$setCurrentEntity(null);
        }

    }

    @Inject(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", shift = At.Shift.AFTER))
    private void post_render_renderStatic(LivingEntity $$0, ItemStack $$1, ItemDisplayContext $$2, boolean $$3, PoseStack $$4, MultiBufferSource $$5, Level $$6, int $$7, int $$8, int $$9, CallbackInfo ci){
        this.craft_of_the_wild$setCurrentEntity($$0);
    }

    @Override
    public void craft_of_the_wild$setCurrentEntity(@Nullable LivingEntity entity) {
        this.craft_of_the_wild$currentEntity = entity;
    }

    @Override
    public @Nullable LivingEntity craft_of_the_wild$getCurrentEntity() {
        return this.craft_of_the_wild$currentEntity;
    }
}
