package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.client.renderer.RadiantItemBEWLR;
import com.Thelnfamous1.craft_of_the_wild.item.RadiantItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(RadiantItem.class)
public abstract class RadiantItemMixin extends Item {
    public RadiantItemMixin(Properties properties) {
        super(properties);
    }

    /*
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RadiantItemBEWLR renderer = null;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new RadiantItemBEWLR();

                return renderer;
            }
        });
    }
     */
}
