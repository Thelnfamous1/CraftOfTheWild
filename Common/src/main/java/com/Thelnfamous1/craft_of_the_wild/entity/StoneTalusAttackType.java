package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public enum StoneTalusAttackType implements AnimatedAttacker.AttackType, StringRepresentable {
    NONE(0, "none", 0, 0),
    POUND(1, "pound", COTWUtil.secondsToTicks(0.92F), COTWUtil.secondsToTicks(3.5833F)),
    THROW(2, "throw", COTWUtil.secondsToTicks(0.79F), COTWUtil.secondsToTicks(5.9167F)),
    HEADBUTT(3, "headbutt", COTWUtil.secondsToTicks(2.5F), COTWUtil.secondsToTicks(6.4583F)),
    PUNCH(4, "punch", COTWUtil.secondsToTicks(0.54F), COTWUtil.secondsToTicks(1.25F));

    public static final StringRepresentable.EnumCodec<StoneTalusAttackType> CODEC = StringRepresentable.fromEnum(StoneTalusAttackType::values);
    private static final IntFunction<StoneTalusAttackType> BY_ID = ByIdMap.continuous(StoneTalusAttackType::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

    private final int id;
    private final String key;
    private final int attackAnimationActionPoint;
    private final int attackAnimationLength;

    StoneTalusAttackType(int id, String key, int attackAnimationActionPoint, int attackAnimationLength) {
        this.id = id;
        this.key = key;
        this.attackAnimationActionPoint = attackAnimationActionPoint;
        this.attackAnimationLength = attackAnimationLength;
    }

    public static StoneTalusAttackType byId(int id){
        return BY_ID.apply(id);
    }

    @Nullable
    public static StoneTalusAttackType byName(String pName) {
        return CODEC.byName(pName);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getAttackAnimationActionPoint() {
        return this.attackAnimationActionPoint;
    }

    @Override
    public int getAttackAnimationLength() {
        return this.attackAnimationLength;
    }

    @Override
    public String getSerializedName() {
        return this.key;
    }
}