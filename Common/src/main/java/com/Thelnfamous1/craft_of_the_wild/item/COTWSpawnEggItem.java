package com.Thelnfamous1.craft_of_the_wild.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class COTWSpawnEggItem extends SpawnEggItem {
    private static final List<COTWSpawnEggItem> EGGS = new ArrayList<>();
    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public COTWSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties properties) {
        //noinspection ConstantConditions
        super(null, backgroundColor, highlightColor, properties);
        this.typeSupplier = type;
        EGGS.add(this);
    }

    public static List<COTWSpawnEggItem> getEggs() {
        return Collections.unmodifiableList(EGGS);
    }

    public EntityType<? extends Mob> type(){
        return this.typeSupplier.get();
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag data) {
        if (data != null && data.contains("EntityTag", Tag.TAG_COMPOUND)) {
            CompoundTag entityTag = data.getCompound("EntityTag");
            if (entityTag.contains("id", Tag.TAG_STRING)) {
                return EntityType.byString(entityTag.getString("id")).orElse(this.type());
            }
        }
        return this.type();
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.type().requiredFeatures();
    }
}