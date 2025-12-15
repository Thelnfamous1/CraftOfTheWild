package com.Thelnfamous1.craft_of_the_wild.entity.talus;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.IgneoTalusArm;
import com.Thelnfamous1.craft_of_the_wild.entity.talus.arm.StoneTalusArm;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
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

    public static void applyBurnEffect(Entity target) {
        if(!target.fireImmune()){
            int remainingTicksOnFire = Math.max(0, target.getRemainingFireTicks());
            target.setRemainingFireTicks(remainingTicksOnFire + (4 * 20));
            COTWCommon.debug(Constants.DEBUG_IGNEO_TALUS, "Talus burned {} for {} ticks and they are {}", target, target.getRemainingFireTicks(), (target.isOnFire() ? "on fire" : "not on fire"));
        }
    }

    @Override
    protected void onAttackStarted(StoneTalusAttackType currentAttackType) {
        switch (currentAttackType){
            case HEADBUTT -> this.playSoundEvent(SoundInit.IGNEO_TALUS_HEADBUTT.get());
            case STUN -> this.playSoundEvent(SoundInit.STONE_TALUS_STUN.get());
            case PUNCH -> this.playSoundEvent(SoundInit.IGNEO_TALUS_PUNCH.get());
            case POUND -> this.playSoundEvent(SoundInit.IGNEO_TALUS_POUND.get());
            case THROW -> this.playSoundEvent(SoundInit.STONE_TALUS_THROW_ARMS.get());
            case SHAKE -> this.playSoundEvent(SoundInit.STONE_TALUS_SHAKE.get());
        }
    }

    @Override
    protected void playAttackSound(StoneTalusAttackType currentAttackType, AttackPoint currentAttackPoint) {
        if(!this.level().isClientSide){
            if(currentAttackPoint.damageMode() == AttackPoint.DamageMode.AREA_OF_EFFECT){
                switch (currentAttackType){
                    case PUNCH, POUND -> COTWUtil.playVanillaExplosionSound(this, SoundInit.IGNEO_TALUS_BREAK_ROCKS.get(), 4.0F);
                    case HEADBUTT, STUN -> COTWUtil.playVanillaExplosionSound(this, SoundEvents.GENERIC_EXPLODE, 4.0F);
                }

            }
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
        applyBurnEffect(target);
    }

    @Override
    protected StoneTalusArm createArmProjectile(double xDist, double yDist, double zDist) {
        return new IgneoTalusArm(this.level(), this, xDist, yDist, zDist);
    }

    @Override
    protected void spawnClientSideAOEParticles(AABB attackBox, BlockPos blockPos, Vec3 particlePos) {
        COTWUtil.spawnSmashAttackParticles(this.level(), ParticleTypes.FLAME, particlePos, COTWUtil.getXZSize(attackBox), 750);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(this.tickCount % 20 == 0){
            BrainUtils.withMemory(this, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, nvle -> {
                nvle.find(le ->
                        !le.getItemBySlot(EquipmentSlot.FEET).is(COTWTags.BURN_IMMUNE_WEARABLES)
                                && COTWSharedAi.isEntityAttackable(this, le, getTargetingRange(this))
                                && this.isTargetOnTopOfMe(le)
                                && !le.isOnFire())
                        .forEach(IgneoTalus::applyBurnEffect);
            });
        }
    }
}
