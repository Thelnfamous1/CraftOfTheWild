package com.Thelnfamous1.craft_of_the_wild.entity.talus;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class StoneTalusRare extends StoneTalus {
    public StoneTalusRare(EntityType<? extends StoneTalusRare> type, Level level) {
        super(type, level);
        this.xpReward = 60;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return StoneTalus.createAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.ARMOR, 6.0D);
    }

    public static <T extends StoneTalusRare> boolean checkRareTalusSpawnRules(EntityType<T> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return StoneTalus.checkStoneTalusSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
    }

    @Override
    protected double getAttackBaseDamageModifier(StoneTalusAttackType currentAttackType, AttackPoint currentAttackPoint) {
        if(currentAttackType == StoneTalusAttackType.PUNCH || currentAttackType == StoneTalusAttackType.POUND){
            return 1;
        }
        return super.getAttackBaseDamageModifier(currentAttackType, currentAttackPoint);
    }
}
