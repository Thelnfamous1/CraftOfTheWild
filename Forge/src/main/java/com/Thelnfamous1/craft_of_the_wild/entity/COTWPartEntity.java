package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

public class COTWPartEntity<T extends LivingEntity & MultipartEntity> extends PartEntity<T> {

    private final PartEntityController.Info info;
    private final EntityDimensions size;

    public COTWPartEntity(T parent, PartEntityController.Info info) {
        super(parent);
        this.info = info;
        this.size = EntityDimensions.scalable(info.width(), info.height()).scale(info.scale());
        this.refreshDimensions();
    }

    public static <T extends LivingEntity & MultipartEntity> void basicTicker(COTWPartEntity<T> part){
        T parent = part.getParent();
        float yRot = part.info.bodyPart() ? parent.yBodyRot : parent.getYRot();
        float xRot = parent.getXRot();
        Vec3 offsetVec = new Vec3(part.info.xOffset(), part.info.yOffset(), part.info.zOffset())
                .yRot(-yRot * Mth.DEG_TO_RAD)
                .xRot(-xRot * Mth.DEG_TO_RAD)
                .scale(parent.getScale() * part.info.scale());
        part.setPos(parent.getX() + offsetVec.x, parent.getY() + offsetVec.y, parent.getZ() + offsetVec.z);
    }

    public PartEntityController.Info getInfo(){
        return this.info;
    }

    public String getPartName() {
        return this.info.name();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.getParent().getPickResult();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return !this.isInvulnerableTo(pSource) && this.getParent().hurt(this, pSource, pAmount);
    }

    @Override
    public boolean is(Entity pEntity) {
        return this == pEntity || this.getParent() == pEntity;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}