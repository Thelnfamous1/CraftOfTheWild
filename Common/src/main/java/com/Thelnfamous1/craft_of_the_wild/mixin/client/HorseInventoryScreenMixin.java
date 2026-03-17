package com.Thelnfamous1.craft_of_the_wild.mixin.client;

import com.Thelnfamous1.craft_of_the_wild.duck.ChestableHorse;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryScreen.class)
public abstract class HorseInventoryScreenMixin extends AbstractContainerScreen<HorseInventoryMenu> {

    @Shadow @Final private AbstractHorse horse;

    @Shadow @Final private static ResourceLocation HORSE_INVENTORY_LOCATION;

    public HorseInventoryScreenMixin(HorseInventoryMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Inject(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;isSaddleable()Z"))
    private void inject_renderCustomInventoryHorse_renderBg(GuiGraphics pGuiGraphics, float $$1, int $$2, int $$3, CallbackInfo ci){
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.horse instanceof ChestableHorse chestableHorse) {
            if (chestableHorse.craft_of_the_wild$hasChest()) {
                pGuiGraphics.blit(HORSE_INVENTORY_LOCATION, i + 79, j + 17, 0, this.imageHeight, chestableHorse.craft_of_the_wild$getInventoryColumns() * 18, 54);
            }
        }
    }
}
