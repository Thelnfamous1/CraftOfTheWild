package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.duck.ChestableHorse;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
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

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin extends AbstractContainerMenu {
    @Shadow @Final private AbstractHorse horse;

    @Shadow @Final private Container horseContainer;

    protected HorseInventoryMenuMixin(@Nullable MenuType<?> $$0, int $$1) {
        super($$0, $$1);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/HorseInventoryMenu;hasChest(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;)Z"))
    private boolean post_init(HorseInventoryMenu instance, AbstractHorse pHorse, Operation<Boolean> original){
        if (this.craft_of_the_wild$hasChest(this.horse)) {
            for(int k = 0; k < 3; ++k) {
                int inventoryColumns = ((ChestableHorse) this.horse).craft_of_the_wild$getInventoryColumns();
                for(int l = 0; l < inventoryColumns; ++l) {
                    this.addSlot(new Slot(this.horseContainer, 2 + l + k * inventoryColumns, 80 + l * 18, 18 + k * 18));
                }
            }
            return false;
        } else{
            return original.call(instance, pHorse);
        }
    }

    @Unique
    private boolean craft_of_the_wild$hasChest(AbstractHorse horse){
        return horse instanceof ChestableHorse chestableHorse && chestableHorse.craft_of_the_wild$hasChest();
    }
}
