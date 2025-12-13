package com.Thelnfamous1.craft_of_the_wild.entity.talus;

import com.Thelnfamous1.craft_of_the_wild.duck.FreezeAttackVictim;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.FrostTalusArm;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.StoneTalusArm;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtils;

public class FrostTalus extends StoneTalus {
    public FrostTalus(EntityType<? extends FrostTalus> type, Level level) {
        super(type, level);
        this.xpReward = 80;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return StoneTalus.createAttributes()
                .add(Attributes.MAX_HEALTH, 550.0D)
                .add(Attributes.ARMOR, 8.0D);
    }

    public static <T extends FrostTalus> boolean checkFrostTalusSpawnRules(EntityType<T> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return StoneTalus.checkStoneTalusSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
    }

    public static void applyFreezeEffect(Entity target) {
        if(target.canFreeze()){
            int ticksRequiredToFreeze = target.getTicksRequiredToFreeze();
            int frozenTicksToAdd = (int) (ticksRequiredToFreeze / 2.0D);
            frozenTicksToAdd = Math.min(ticksRequiredToFreeze - target.getTicksFrozen(), frozenTicksToAdd);
            FreezeAttackVictim.applyFreezeEffect(target, frozenTicksToAdd, 160);
        }
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
    protected void doPostDamageEffects(Entity target) {
        applyFreezeEffect(target);
    }

    @Override
    protected StoneTalusArm createArmProjectile(double xDist, double yDist, double zDist) {
        return new FrostTalusArm(this.level(), this, xDist, yDist, zDist);
    }

    @Override
    protected void spawnClientSideAOEParticles(AABB attackBox, BlockPos blockPos, Vec3 particlePos) {
        COTWUtil.spawnSmashAttackParticles(this.level(), ParticleTypes.SNOWFLAKE, particlePos, COTWUtil.getXZSize(attackBox), 750);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(this.tickCount % 20 == 0){
            BrainUtils.withMemory(this, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, nvle -> {
                nvle.find(le ->
                                !le.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FREEZE_IMMUNE_WEARABLES)
                                        && COTWSharedAi.isEntityAttackable(this, le, getTargetingRange(this))
                                        && this.isTargetOnTopOfMe(le)
                                        && !le.isFullyFrozen())
                        .forEach(FrostTalus::applyFreezeEffect);
            });
        }
    }
}
