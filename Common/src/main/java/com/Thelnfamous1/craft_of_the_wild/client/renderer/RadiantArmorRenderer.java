package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.item.RadiantArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;

public class RadiantArmorRenderer extends GeoArmorRenderer<RadiantArmorItem> {
	private static final ResourceLocation GLOW = COTWCommon.getResourceLocation("textures/item/armor/radiant_glow.png");
	protected GeoBone waist = null;
	public RadiantArmorRenderer() {
		super(new DefaultedItemGeoModel<>(COTWCommon.getResourceLocation("armor/radiant")));
		this.addRenderLayer(new AutoGlowingGeoLayer<>(this){

			/*
			@Override
			protected RenderType getRenderType(RadiantArmorItem animatable) {
				return RenderType.eyes(this.getTextureResource(animatable));
			}
			 */

			@Override
			protected ResourceLocation getTextureResource(RadiantArmorItem animatable) {
				//return super.getTextureResource(animatable);
				return GLOW;
			}

			@Override
			public void render(PoseStack poseStack, RadiantArmorItem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
				Entity wearer = RadiantArmorRenderer.this.currentEntity;
				if (wearer == null) return;

				Level level = wearer.level();
				BlockPos pos = wearer.blockPosition();
				float localBrightness = level.getMaxLocalRawBrightness(pos);

				// Compute an ambient darkness factor based on world time
				float timeOfDay = level.getTimeOfDay(partialTick); // 0 to 1 over a full day
				float nightFactor = Mth.cos(timeOfDay * Mth.TWO_PI) * 0.5F + 0.5F;
				// nightFactor is about 0 at midnight, about 1 at noon

				// Darken perceived brightness at night
				float perceivedBrightness = localBrightness * nightFactor;

				float alpha = Mth.clamp(1.0F - (perceivedBrightness / 15.0F), 0.0F, 1.0F);
                RenderType emissiveRenderType = getRenderType(animatable);

                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, emissiveRenderType,
                        bufferSource.getBuffer(emissiveRenderType), partialTick, 15728640, OverlayTexture.NO_OVERLAY,
                        1, 1, 1, alpha);
            }

		});
	}

	@Override
	public RenderType getRenderType(RadiantArmorItem animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityCutout(texture); // Allows armor to render emissive layers, otherwise they are simply not rendered
	}

	@Override
	protected void grabRelevantBones(BakedGeoModel bakedModel) {
		super.grabRelevantBones(bakedModel);

		if (this.lastModel == bakedModel)
			return;
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
}