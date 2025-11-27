package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import net.minecraft.world.item.ArmorItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BokoblinArmorRenderer<T extends ArmorItem & GeoItem> extends GeoArmorRenderer<T> {
	public BokoblinArmorRenderer() {
		super(new DefaultedItemGeoModel<>(COTWCommon.getResourceLocation("armor/bokoblin")));
		//this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
		this.withScale(1.1F);
	}
}