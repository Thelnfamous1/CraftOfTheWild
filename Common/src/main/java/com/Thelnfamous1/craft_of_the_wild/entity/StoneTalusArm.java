package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.init.DamageTypeInit;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StoneTalusArm extends AbstractHurtingProjectile implements GeoEntity, StoneTalusBase {
    protected static final EntityDataAccessor<Boolean> DATA_BATTLE_ID = SynchedEntityData.defineId(StoneTalusArm.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private double baseDamage = 32.0D;

    public StoneTalusArm(EntityType<? extends StoneTalusArm> type, Level level) {
        super(type, level);
    }

    public StoneTalusArm(Level level, LivingEntity shooter, double xDist, double yDist, double zDist) {
        super(EntityInit.STONE_TALUS_ARM.get(), shooter, xDist, yDist, zDist, level);
    }

    public StoneTalusArm(Level level, double x, double y, double z, double xDist, double yDist, double zDist) {
        super(EntityInit.STONE_TALUS_ARM.get(), x, y, z, xDist, yDist, zDist, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BATTLE_ID, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("damage", Tag.TAG_ANY_NUMERIC)) {
            this.baseDamage = tag.getDouble("damage");
        }
        this.readBattleFromTag(tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("damage", this.baseDamage);
        this.writeBattleToTag(tag);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            COTWUtil.playVanillaExplosionSound(this);
            COTWUtil.spawnVanillaExplosionParticles(((ServerLevel) this.level()), 1, this.position());
            if(Services.PLATFORM.canEntityGrief(this.level(), this)){
                AABB searchBox = this.getBoundingBox().inflate(0.2D);
                COTWUtil.destroyBlocksInBoundingBox(searchBox, this.level(), this, StoneTalus::canDestroy);
            }
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult ehr) {
        super.onHitEntity(ehr);
        if (!this.level().isClientSide) {
            Entity target = ehr.getEntity();
            Entity owner = this.getOwner();
            boolean hurt = target.hurt(DamageTypeInit.stoneTalusArm(this, owner), (float) this.getBaseDamage());
            if (hurt && owner instanceof LivingEntity shooter) {
                if (target.isAlive()) {
                    this.doEnchantDamageEffects(shooter, target);
                }
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public void setBaseDamage(double pBaseDamage) {
        this.baseDamage = pBaseDamage;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public boolean isBattle() {
        return this.entityData.get(DATA_BATTLE_ID);
    }

    @Override
    public void setBattle(boolean battle) {
        this.entityData.set(DATA_BATTLE_ID, battle);
    }
}
