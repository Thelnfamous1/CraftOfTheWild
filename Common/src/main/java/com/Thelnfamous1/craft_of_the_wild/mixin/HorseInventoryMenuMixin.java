package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.duck.ChestableHorse;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin extends AbstractContainerMenu {
    @Shadow @Final private AbstractHorse horse;

    protected HorseInventoryMenuMixin(@Nullable MenuType<?> $$0, int $$1) {
        super($$0, $$1);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void post_init(int $$0, Inventory $$1, Container pContainer, AbstractHorse $$3, CallbackInfo ci){
        if (this.craft_of_the_wild$hasChest(this.horse)) {
            for(int k = 0; k < 3; ++k) {
                int inventoryColumns = ((ChestableHorse) this.horse).craft_of_the_wild$getInventoryColumns();
                for(int l = 0; l < inventoryColumns; ++l) {
                    this.addSlot(new Slot(pContainer, 2 + l + k * inventoryColumns, 80 + l * 18, 18 + k * 18));
                }
            }
        }
    }

    @Unique
    private boolean craft_of_the_wild$hasChest(AbstractHorse horse){
        return horse instanceof ChestableHorse chestableHorse && chestableHorse.craft_of_the_wild$hasChest();
    }
}
