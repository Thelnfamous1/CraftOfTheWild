package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

public class COTWForgePartEntity<T extends LivingEntity & COTWMultipartEntity> extends PartEntity<T> implements COTWPartEntity{

    private final PartEntityController.PartInfo partInfo;
    private final EntityDimensions defaultSize;
    private EntityDimensions currentSize;
    private final PartEntityController.PartResizer<T> partResizer;

    public COTWForgePartEntity(T parent, PartEntityController.PartInfo partInfo, PartEntityController.PartResizer<T> partResizer) {
        super(parent);
        this.partInfo = partInfo;
        this.defaultSize = EntityDimensions.scalable(partInfo.width(), partInfo.height()).scale(partInfo.scale());
        this.currentSize = this.defaultSize;
        this.partResizer = partResizer;
        this.refreshDimensions();
    }

    public static <T extends LivingEntity & COTWMultipartEntity> PartEntityController.PartTicker<T, COTWForgePartEntity<T>> typedPartTicker(PartEntityController.PartTicker<T, ? extends Entity> untypedTicker){
        return (PartEntityController.PartTicker<T, COTWForgePartEntity<T>>) untypedTicker;
    }

    @Override
    public PartEntityController.PartInfo getInfo(){
        return this.partInfo;
    }

    public String getPartName() {
        return this.partInfo.name();
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
        if(this.partResizer != null){ // null during Entity superclass construction, as this method is called within it
            this.currentSize = this.partResizer.resizePart(this, this.getParent(), this.defaultSize);
        }
        return this.currentSize;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

}