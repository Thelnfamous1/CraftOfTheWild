package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.entity.ai.navigation.COTWGroundPathNavigation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class COTWMob extends PathfinderMob implements GeoEntity {
    protected static final EntityDataAccessor<Boolean> DATA_WALKING = SynchedEntityData.defineId(COTWMob.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected int customDeathTime;

    public COTWMob(EntityType<? extends COTWMob> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new COTWGroundPathNavigation(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WALKING, false);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("COTWDeathTime", Tag.TAG_ANY_NUMERIC)) {
            this.customDeathTime = tag.getShort("COTWDeathTime");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("COTWDeathTime", (short) this.customDeathTime);
    }

    protected abstract int getMaxDeathTime();

    @Override
    protected void tickDeath() {
        ++this.customDeathTime;
        if (this.customDeathTime >= this.getMaxDeathTime() && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    public boolean isWalking(){
        return this.entityData.get(DATA_WALKING);
    }

    protected void setIsWalking(boolean isWalking){
        this.entityData.set(DATA_WALKING, isWalking);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(!this.level().isClientSide){
            this.setIsWalking(this.zza > 0);
        }
    }
}
