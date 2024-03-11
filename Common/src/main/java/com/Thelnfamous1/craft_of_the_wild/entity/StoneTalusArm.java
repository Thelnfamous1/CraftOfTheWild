package com.Thelnfamous1.craft_of_the_wild.entity;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.init.DamageTypeInit;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

public class StoneTalusArm extends AbstractHurtingProjectile implements GeoEntity, StoneTalusBase {
    protected static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(StoneTalusArm.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private double baseDamage = 24.0D;
    private double radius = 1 * StoneTalus.LOGICAL_SCALE;
    @Nullable
    private Variant cachedVariant;

    public StoneTalusArm(EntityType<? extends StoneTalusArm> type, Level level) {
        super(type, level);
    }

    public StoneTalusArm(Level level, LivingEntity shooter, double xDist, double yDist, double zDist) {
        super(EntityInit.STONE_TALUS_ARM.get(), shooter, xDist, yDist, zDist, level);
        this.applyPowerScaling();
        COTWCommon.debug(Constants.DEBUG_STONE_TALUS_ARM, "{} was created with {}, {}, {} power within [-3.9, 3.9] range: {}, {}, {}",
                this,
                this.xPower,
                this.yPower,
                this.zPower,
                COTWUtil.isInRange(this.xPower, -3.9, 3.9),
                COTWUtil.isInRange(this.yPower, -3.9, 3.9),
                COTWUtil.isInRange(this.zPower, -3.9, 3.9));
    }

    protected void applyPowerScaling() {
        this.xPower *= 4;
        this.yPower *= 4;
        this.zPower *= 4;
    }

    public StoneTalusArm(Level level, double x, double y, double z, double xDist, double yDist, double zDist) {
        super(EntityInit.STONE_TALUS_ARM.get(), x, y, z, xDist, yDist, zDist, level);
        this.applyPowerScaling();
        COTWCommon.debug(Constants.DEBUG_STONE_TALUS_ARM, "{} was created with {}, {}, {} power within [-3.9, 3.9] range: {}, {}, {}",
                this,
                this.xPower,
                this.yPower,
                this.zPower,
                COTWUtil.isInRange(this.xPower, -3.9, 3.9),
                COTWUtil.isInRange(this.yPower, -3.9, 3.9),
                COTWUtil.isInRange(this.zPower, -3.9, 3.9));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, Variant.NORMAL.getId());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if(dataAccessor.equals(DATA_VARIANT_ID)){
            this.cachedVariant = null;
        }
    }

    @Override
    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT_ID, variant.getId());
    }

    @Override
    public Variant getVariant() {
        if(this.cachedVariant == null){
            this.cachedVariant = Variant.byId(this.entityData.get(DATA_VARIANT_ID));
        }
        return this.cachedVariant;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("damage", Tag.TAG_ANY_NUMERIC)) {
            this.baseDamage = tag.getDouble("damage");
        }
        if (tag.contains("radius", Tag.TAG_ANY_NUMERIC)) {
            this.radius = tag.getDouble("radius");
        }
        this.readVariantFromTag(tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("damage", this.baseDamage);
        tag.putDouble("radius", this.radius);
        this.writeVariantToTag(tag);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if(this.getOwner() instanceof COTWMultipartEntity mpe && COTWMultipartEntity.partEntityList(mpe).contains(entity)){
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            double radius = this.getRadius();
            COTWUtil.playVanillaExplosionSound(this, SoundInit.STONE_TALUS_BREAK_ROCKS.get(), 4.0F);
            double attackSize = radius * 2;

            Vec3 location = hitResult.getLocation();
            AABB attackBox = AABB.ofSize(this.position(), attackSize, attackSize, attackSize).inflate(1.0D);
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_ARM, "Created attack box of size {} for {}", attackBox.getSize(), this);
            if(Constants.DEBUG_STONE_TALUS_ARM) COTWUtil.sendHitboxParticles(attackBox, this.level());
            LivingEntity owner = this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;
            //noinspection ConstantConditions
            List<LivingEntity> targets = this.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, owner, attackBox);
            targets.forEach(target -> this.applyDamage(target, owner));
            if(Services.PLATFORM.canEntityGrief(this.level(), this)){
                COTWUtil.destroyBlocksInBoundingBox(attackBox, this.level(), this, StoneTalus::canDestroy);
            }
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_ARM_PARTICLES, "{} is spawning particles at {}", this, this.position());
            Vec3 particlePos = this.position();
            BlockPos blockPos = BlockPos.containing(particlePos);
            if(!level().getBlockState(blockPos.below()).canBeReplaced()){
                particlePos = new Vec3(particlePos.x, blockPos.getY(), particlePos.z);
                Services.PLATFORM.sendCircleParticlesPacket(ParticleTypes.CAMPFIRE_COSY_SMOKE, particlePos.x, particlePos.y, particlePos.z, this.getRadius(), 100);
            }
            this.discard();
        }
    }

    private void applyDamage(LivingEntity target, @Nullable LivingEntity owner){
        boolean hurt = target.hurt(DamageTypeInit.stoneTalusArm(this, owner), (float) this.getBaseDamage());
        if (hurt && owner != null && target.isAlive()) {
            this.doEnchantDamageEffects(owner, target);
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

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return this.radius;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.applyPowerScaling();
        COTWCommon.debug(Constants.DEBUG_STONE_TALUS_ARM, "{} was recreated with {}, {}, {} power within [-3.9, 3.9] range: {}, {}, {}",
                this,
                this.xPower,
                this.yPower,
                this.zPower,
                COTWUtil.isInRange(this.xPower, -3.9, 3.9),
                COTWUtil.isInRange(this.yPower, -3.9, 3.9),
                COTWUtil.isInRange(this.zPower, -3.9, 3.9));
    }
}
