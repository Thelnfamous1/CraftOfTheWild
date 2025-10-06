package com.Thelnfamous1.craft_of_the_wild.util;

import net.minecraft.world.entity.decoration.PaintingVariant;

public class COTWPaintingVariant extends PaintingVariant {
    private final String author;

    public COTWPaintingVariant(int $$0, int $$1, String author) {
        super($$0, $$1);
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }
}
