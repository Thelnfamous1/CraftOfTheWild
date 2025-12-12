package com.Thelnfamous1.craft_of_the_wild.entity.talus;

import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.IgneoTalusArm;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.StoneTalusArm;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class IgneoTalus extends StoneTalus {
    public IgneoTalus(EntityType<? extends IgneoTalus> type, Level level) {
        super(type, level);
        this.xpReward = 80;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return StoneTalus.createAttributes()
                .add(Attributes.MAX_HEALTH, 550.0D)
                .add(Attributes.ARMOR, 8.0D);
    }

    public static <T extends IgneoTalus> boolean checkIgneoTalusSpawnRules(EntityType<T> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return StoneTalus.checkStoneTalusSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
    }

    @Override
    protected double getAttackBaseDamageModifier(StoneTalusAttackType currentAttackType, AttackPoint currentAttackPoint) {
        if(currentAttackType == StoneTalusAttackType.PUNCH || currentAttackType == StoneTalusAttackType.POUND){
            return 1;
        }
        return super.getAttackBaseDamageModifier(currentAttackType, currentAttackPoint);
    }

    @Override
    protected void setSpawnVariant(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag) {

    }

    @Override
    protected StoneTalusArm createArmProjectile(double xDist, double yDist, double zDist) {
        return new IgneoTalusArm(this.level(), this, xDist, yDist, zDist);
    }
}
