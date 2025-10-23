package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.item.RadiantArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
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