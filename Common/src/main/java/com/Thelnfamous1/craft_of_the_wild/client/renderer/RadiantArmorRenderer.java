package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;

public class RadiantArmorRenderer<T extends ArmorItem & GeoItem> extends GeoArmorRenderer<T> {
	private static final ResourceLocation OVERLAY = COTWCommon.getResourceLocation("textures/item/armor/radiant_overlay.png");
	private static final ResourceLocation GLOW = COTWCommon.getResourceLocation("textures/item/armor/radiant_glow.png");
	protected GeoBone waist = null;
	public RadiantArmorRenderer() {
		super(new DefaultedItemGeoModel<>(COTWCommon.getResourceLocation("armor/radiant")));
		this.addRenderLayer(new GeoRenderLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(T animatable) {
                return OVERLAY;
            }

            @Override
            public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if(animatable instanceof DyeableLeatherItem){
					RenderType armorRenderType = RenderType.armorCutoutNoCull(this.getTextureResource(animatable));

					getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType,
							bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
							1, 1, 1, 1);
				}
            }
        });
		this.addRenderLayer(new AutoGlowingGeoLayer<>(this){

			/*
			@Override
			protected RenderType getRenderType(RadiantArmorItem animatable) {
				return RenderType.eyes(this.getTextureResource(animatable));
			}
			 */

			@Override
			protected ResourceLocation getTextureResource(T animatable) {
				//return super.getTextureResource(animatable);
				return GLOW;
			}

			@Override
			public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
				Entity wearer = RadiantArmorRenderer.this.currentEntity;
				if (wearer == null) return;

				float alpha = COTWUtil.getLocalDarknessFactor(partialTick, wearer.level(), wearer.blockPosition());
				RenderType emissiveRenderType = getRenderType(animatable);

                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, emissiveRenderType,
                        bufferSource.getBuffer(emissiveRenderType), partialTick, 15728640, OverlayTexture.NO_OVERLAY,
                        1, 1, 1, alpha);
            }

		});
	}



	@Override
	public RenderType getRenderType(T animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityCutout(texture); // Allows armor to render emissive layers, otherwise they are simply not rendered
	}

	@Override
	protected void grabRelevantBones(BakedGeoModel bakedModel) {
		if (this.lastModel == bakedModel)
			return;
		super.grabRelevantBones(bakedModel);
		this.waist = getWaistBone();
	}

	@Nullable
	public GeoBone getWaistBone() {
		return this.model.getBone("armorWaist").orElse(null);
	}

	@Override
	protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
		super.applyBoneVisibilityBySlot(currentSlot);
		setBoneVisible(this.waist, currentSlot == EquipmentSlot.LEGS);
	}

	@Override
	protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
		super.applyBaseTransformations(baseModel);
		if (this.waist != null) {
			ModelPart waistPart = baseModel.body;

			RenderUtils.matchModelPartRot(waistPart, this.waist);
			this.waist.updatePosition(waistPart.x, -waistPart.y, waistPart.z);
		}
	}

	@Override
	public void setAllVisible(boolean pVisible) {
		super.setAllVisible(pVisible);
		setBoneVisible(this.waist, pVisible);
	}

	@Override
	public Color getRenderColor(T animatable, float partialTick, int packedLight) {
		ItemStack stack = this.getCurrentStack();
		if(animatable instanceof DyeableLeatherItem dyeableLeatherItem && dyeableLeatherItem.hasCustomColor(stack)){
			int color = dyeableLeatherItem.getColor(stack);
			float r = (float)((color >> 16) & 0xFF) / 255f;
			float g = (float)((color >> 8) & 0xFF) / 255f;
			float b = (float)(color & 0xFF) / 255f;
			return Color.ofRGB(r, g, b);
		} else{
			return super.getRenderColor(animatable, partialTick, packedLight);
		}
    }
}