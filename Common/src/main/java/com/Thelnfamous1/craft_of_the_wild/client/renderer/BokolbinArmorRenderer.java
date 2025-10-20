package com.Thelnfamous1.craft_of_the_wild.client.renderer;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.item.BokolbinArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class BokolbinArmorRenderer extends GeoArmorRenderer<BokolbinArmorItem> {
	public BokolbinArmorRenderer() {
		super(new DefaultedItemGeoModel<>(COTWCommon.getResourceLocation("armor/bokolbin")));
		//this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
		this.withScale(1.1F);
	}
}