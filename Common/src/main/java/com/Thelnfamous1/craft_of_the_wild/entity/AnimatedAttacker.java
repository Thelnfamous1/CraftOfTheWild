package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import java.util.Optional;

public interface AnimatedAttacker<A extends AnimatedAttacker.AttackType> {
    byte ATTACK_EVENT_ID = 4;

    static <A extends AnimatedAttacker.AttackType> Optional<A> optionalCurrentAttackType(AnimatedAttacker<A> attacker){
        return Optional.ofNullable(attacker.getCurrentAttackType());
    }

    static <A extends AnimatedAttacker.AttackType> boolean hasCurrentAttackType(AnimatedAttacker<A> attacker, A attackType){
        return attackType.equals(attacker.getCurrentAttackType());
    }

    default boolean isAttackAnimationInProgress(){
        if(this.isAttacking()){
            A currentAttackType = this.getCurrentAttackType();
            return currentAttackType != null && this.getTicksSinceAttackStarted() <= currentAttackType.getAttackDuration();
        }
        return false;
    }

    boolean isAttacking();

    int getTicksSinceAttackStarted();

    @Nullable
    default AttackPoint getCurrentAttackPoint(){
        A currentAttackType = this.getCurrentAttackType();
        if(currentAttackType == null){
            return null;
        }
        for(AttackPoint attackPoint : this.getCurrentAttackType().getAttackPoints()){
            if(this.getTicksSinceAttackStarted() == attackPoint.ticksSinceStart())
                return attackPoint;
        }
        return null;
    }

    @Nullable A getCurrentAttackType();

    void setCurrentAttackType(@Nullable A attackType);

    interface AttackType{
        int getId();

        ImmutableList<AttackPoint> getAttackPoints();

        int getAttackDuration();

        String getKey();

    }

    record AttackPoint(int ticksSinceStart, double baseDamageModifier, DamageMode damageMode){
        public static AttackPoint of(float secondsSinceStart, double baseDamageModifier, DamageMode damageMode){
            return of(COTWUtil.secondsToTicks(secondsSinceStart), baseDamageModifier, damageMode);
        }
        public static AttackPoint of(int ticksSinceStart, double baseDamageModifier, DamageMode damageMode){
            return new AttackPoint(ticksSinceStart, baseDamageModifier, damageMode);
        }

        public enum DamageMode{
            MELEE,
            AREA_OF_EFFECT,
            RANGED
        }
    }
}