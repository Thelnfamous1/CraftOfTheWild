package com.Thelnfamous1.craft_of_the_wild.entity;

import javax.annotation.Nullable;

import java.util.Optional;

public interface AnimatedAttacker<A extends AnimatedAttacker.AttackType> {
    byte ATTACK_EVENT_ID = 4;
    default boolean isAttackAnimationInProgress(){
        A currentAttackType = this.getCurrentAttackType();
        return currentAttackType != null && this.getTicksSinceLastAttack() <= currentAttackType.getAttackAnimationLength();
    }

    int getTicksSinceLastAttack();

    default boolean isTimeToAttack(){
        A currentAttackType = this.getCurrentAttackType();
        if(currentAttackType == null){
            return false;
        }
        for(int actionPoint : currentAttackType.getAttackAnimationActionPoints()){
            if(this.getTicksSinceLastAttack() == actionPoint)
                return true;
        }
        return false;
    }

    default Optional<A> getOptionalCurrentAttackType(){
        return Optional.ofNullable(this.getCurrentAttackType());
    }

    @Nullable A getCurrentAttackType();

    void setCurrentAttackType(@Nullable A attackType);

    enum DamageMode{
        MELEE,
        AREA_OF_EFFECT,
        RANGED
    }

    interface AttackType{
        int getId();

        Iterable<Integer> getAttackAnimationActionPoints();

        int getAttackAnimationLength();

        double getBaseDamageModifier();

        DamageMode getDamageMode();

    }
}