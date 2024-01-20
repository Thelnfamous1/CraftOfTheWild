package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public enum StoneTalusAttackType implements AnimatedAttacker.AttackType, StringRepresentable {
    POUND(0, "pound", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(0.92F, 1, AnimatedAttacker.AttackPoint.DamageMode.AREA_OF_EFFECT),
            AnimatedAttacker.AttackPoint.of(3.08F, 1, AnimatedAttacker.AttackPoint.DamageMode.AREA_OF_EFFECT)),
            COTWUtil.secondsToTicks(3.5833F)),
    THROW(1, "throw", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(0.71F, 1.5, AnimatedAttacker.AttackPoint.DamageMode.RANGED),
            AnimatedAttacker.AttackPoint.of(1.96F, 1.5, AnimatedAttacker.AttackPoint.DamageMode.RANGED)),
            COTWUtil.secondsToTicks(5.9167F)),
    HEADBUTT(2, "headbutt", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(2.5F, 5D / 3D, AnimatedAttacker.AttackPoint.DamageMode.AREA_OF_EFFECT)),
            COTWUtil.secondsToTicks(6.4583F)),
    PUNCH(3, "punch", ImmutableList.of(
            AnimatedAttacker.AttackPoint.of(0.54F, 1, AnimatedAttacker.AttackPoint.DamageMode.MELEE)),
            COTWUtil.secondsToTicks(1.25F));

    public static final StringRepresentable.EnumCodec<StoneTalusAttackType> CODEC = StringRepresentable.fromEnum(StoneTalusAttackType::values);
    private static final IntFunction<StoneTalusAttackType> BY_ID = ByIdMap.continuous(StoneTalusAttackType::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

    private final int id;
    private final String key;
    private final ImmutableList<AnimatedAttacker.AttackPoint> attackPoints;
    private final int attackDuration;

    StoneTalusAttackType(int id, String key, ImmutableList<AnimatedAttacker.AttackPoint> attackPoints, int attackDuration) {
        this.id = id;
        this.key = key;
        this.attackPoints = attackPoints;
        this.attackDuration = attackDuration;
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