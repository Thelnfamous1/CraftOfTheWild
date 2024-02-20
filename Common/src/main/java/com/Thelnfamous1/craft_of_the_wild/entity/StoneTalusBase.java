package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.VariantHolder;

import javax.annotation.Nullable;
import java.util.function.IntFunction;

public interface StoneTalusBase extends VariantHolder<StoneTalusBase.Variant> {

    String VARIANT_TAG_KEY = "Variant";

    default boolean isBattle(){
        return this.getVariant().isBattle();
    }

    default void readVariantFromTag(CompoundTag tag) {
        if(tag.contains(VARIANT_TAG_KEY, Tag.TAG_ANY_NUMERIC)){
            this.setVariant(Variant.byId(tag.getInt(VARIANT_TAG_KEY)));
        }
    }

    default void writeVariantToTag(CompoundTag tag) {
        tag.putInt(VARIANT_TAG_KEY, this.getVariant().getId());
    }

    enum Variant implements StringRepresentable {
        NORMAL(0, "normal", false),
        NORMAL_DARK(1, "normal_dark", false),
        BATTLE(2, "battle", true),
        BATTLE_DARK(3, "battle_dark", true);

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StringRepresentable.EnumCodec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private final int id;
        private final String name;
        private final boolean battle;

        Variant(int id, String name, boolean battle){
            this.id = id;
            this.name = name;
            this.battle = battle;
        }

        public static Variant byId(int id) {
            return BY_ID.apply(id);
        }

        @Nullable
        public static Variant byName(String name) {
            return CODEC.byName(name);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return this.name;
        }

        public boolean isBattle() {
            return this.battle;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
