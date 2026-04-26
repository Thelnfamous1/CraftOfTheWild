package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.item.AlternateArmor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;

public class HylianArmorRenderer<T extends ArmorItem & GeoItem> extends GeoArmorRenderer<T> {
	private static final ResourceLocation ALTERNATE = COTWCommon.getResourceLocation("textures/item/armor/hylian_alternate.png");
	protected GeoBone waist = null;

	public HylianArmorRenderer() {
		super(new DefaultedItemGeoModel<>(COTWCommon.getResourceLocation("armor/hylian")));
	}

	@Override
	public ResourceLocation getTextureLocation(T animatable) {
		if(animatable instanceof AlternateArmor alternateArmor && AlternateArmor.usingAlternate(this.currentStack)){
			return ALTERNATE;
		}
		return super.getTextureLocation(animatable);
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
}