package com.Thelnfamous1.craft_of_the_wild.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;

public class COTWRecordItem extends RecordItem {

    private final String author;

    public COTWRecordItem(int $$0, SoundEvent soundEvent, Properties $$2, int $$3, String author) {
        super($$0, soundEvent, $$2, $$3);
        this.author = author;
    }

    public String getAuthor() {
        return this.author;
    }
}
