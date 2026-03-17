package com.Thelnfamous1.craft_of_the_wild.duck;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.item.ItemStack;

public interface SaddleEquipper extends Saddleable {

    void craft_of_the_wild$equipSaddle(SoundSource source, ItemStack saddle);

    ItemStack craft_of_the_wild$getSaddle();

    void craft_of_the_wild$setClientSaddle(ItemStack saddle);
}
