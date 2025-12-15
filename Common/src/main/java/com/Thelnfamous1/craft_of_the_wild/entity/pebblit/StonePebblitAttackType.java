package com.Thelnfamous1.craft_of_the_wild.entity.pebblit;

import com.Thelnfamous1.craft_of_the_wild.entity.AnimatedAttacker;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public enum StonePebblitAttackType implements AnimatedAttacker.AttackType, StringRepresentable {
    POUND(0, "pound", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(0.67F, 1.0D, AnimatedAttacker.AttackPoint.DamageMode.AREA_OF_EFFECT)),
            COTWUtil.secondsToTicks(1.25F));

    public static final EnumCodec<StonePebblitAttackType> CODEC = StringRepresentable.fromEnum(StonePebblitAttackType::values);
    private static final IntFunction<StonePebblitAttackType> BY_ID = ByIdMap.continuous(StonePebblitAttackType::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

    private final int id;
    private final String key;
    private final ImmutableList<AnimatedAttacker.AttackPoint> attackPoints;
    private final int attackDuration;

    StonePebblitAttackType(int id, String key, ImmutableList<AnimatedAttacker.AttackPoint> attackPoints, int attackDuration) {
        this.id = id;
        this.key = key;
        this.attackPoints = attackPoints;
        this.attackDuration = attackDuration;
    }

    public static StonePebblitAttackType byId(int id){
        return BY_ID.apply(id);
    }

    @Nullable
    public static StonePebblitAttackType byName(String pName) {
        return CODEC.byName(pName);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ImmutableList<AnimatedAttacker.AttackPoint> getAttackPoints() {
        return this.attackPoints;
    }

    @Override
    public int getAttackDuration() {
        return this.attackDuration;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getSerializedName() {
        return this.key;
    }

}