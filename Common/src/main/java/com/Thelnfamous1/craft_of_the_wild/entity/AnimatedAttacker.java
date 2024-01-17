package com.Thelnfamous1.craft_of_the_wild.entity;

public interface AnimatedAttacker<A extends AnimatedAttacker.AttackType> {
    byte ATTACK_EVENT_ID = 4;
    default boolean isAttackAnimationInProgress(){
        return this.getTicksSinceLastAttack() <= this.getCurrentAttackType().getAttackAnimationLength();
    }

    int getTicksSinceLastAttack();

    default boolean isTimeToAttack(){
        for(int actionPoint : this.getCurrentAttackType().getAttackAnimationActionPoints()){
            if(this.getTicksSinceLastAttack() == actionPoint)
                return true;
        }
        return false;
    }

    A getCurrentAttackType();

    void setCurrentAttackType(A attackType);

    interface AttackType{
        int getId();

        Iterable<Integer> getAttackAnimationActionPoints();

        int getAttackAnimationLength();
    }
}