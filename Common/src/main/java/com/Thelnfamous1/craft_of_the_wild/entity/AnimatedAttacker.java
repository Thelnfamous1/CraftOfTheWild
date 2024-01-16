package com.Thelnfamous1.craft_of_the_wild.entity;

public interface AnimatedAttacker<A extends AnimatedAttacker.AttackType> {
    byte ATTACK_EVENT_ID = 4;
    default boolean isAttackAnimationInProgress(){
        return this.getTicksSinceLastAttack() < this.getCurrentAttackType().getAttackAnimationLength();
    }

    long getTicksSinceLastAttack();

    long getLastAttackTick();

    void setLastAttackTick(long lastAttackTick);

    default boolean isTimeToAttack(){
        return this.getCurrentAttackType().getAttackAnimationLength() - this.getTicksSinceLastAttack() == this.getCurrentAttackType().getAttackAnimationActionPoint();
    }

    A getCurrentAttackType();

    void setCurrentAttackType(A attackType);

    interface AttackType{
        int getId();

        int getAttackAnimationActionPoint();

        int getAttackAnimationLength();
    }
}