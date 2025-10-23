package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.mixin.client.ItemRendererAccess;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RadiantItemBEWLR extends BlockEntityWithoutLevelRenderer {

    public RadiantItemBEWLR(){
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public RadiantItemBEWLR(BlockEntityRenderDispatcher $$0, EntityModelSet $$1) {
        super($$0, $$1);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {

        LivingEntity currentEntity = ((EntityAwareItemRenderer) Minecraft.getInstance().getItemRenderer())
                                   .craft_of_the_wild$getCurrentEntity();
        Entity contextEntity = currentEntity != null ? currentEntity : stack.getEntityRepresentation();
        Level level = contextEntity != null ? contextEntity.level() : Minecraft.getInstance().level;
        BlockPos pos = contextEntity != null ? contextEntity.blockPosition() : BlockPos.ZERO;

        float darkness = COTWUtil.getLocalDarknessFactor(0.0F, level, pos);

        // Use full-bright if dark enough
        int repackedLight = darkness >= 0.0625F ? COTWUtil.invertPackedLightByDarkness(packedLight, darkness) : packedLight;

        // Render model with full-bright light
        VertexConsumer bufferConsumer = ItemRenderer.getFoilBufferDirect(buffer, RenderType.entityCutoutNoCull(stack.getItem().builtInRegistryHolder().key().location()), true, stack.hasFoil());
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(stack, level, currentEntity, 0);
        ((ItemRendererAccess)Minecraft.getInstance().getItemRenderer()).craft_of_the_wild$callRenderModelLists(bakedmodel, stack, repackedLight, packedOverlay, poseStack, bufferConsumer);
    }
}