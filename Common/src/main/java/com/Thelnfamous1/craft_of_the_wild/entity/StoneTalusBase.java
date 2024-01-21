package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public interface StoneTalusBase {

    String BATTLE_TAG_KEY = "Battle";

    boolean isBattle();

    void setBattle(boolean battle);

    default void readBattleFromTag(CompoundTag tag) {
        if(tag.contains(BATTLE_TAG_KEY, Tag.TAG_ANY_NUMERIC)){
            this.setBattle(tag.getBoolean(BATTLE_TAG_KEY));
        }
    }

    default void writeBattleToTag(CompoundTag tag) {
        tag.putBoolean(BATTLE_TAG_KEY, this.isBattle());
    }
}
