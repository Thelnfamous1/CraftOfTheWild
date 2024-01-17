package com.Thelnfamous1.craft_of_the_wild.mixin;

import com.Thelnfamous1.craft_of_the_wild.entity.COTWPartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StoneTalus.class, remap = false)
public abstract class StoneTalusMixin extends Monster {
    @Shadow(remap = false)
    public abstract Entity[] shadow$getSubEntities();

    @Unique
    private PartEntity<?>[] craft_of_the_wild$forgeParts;

    protected StoneTalusMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "setId", at = @At("RETURN"))
    private void onSetId(int pId, CallbackInfo ci){
        for(int i = 0; i < this.getParts().length; i++){
            COTWPartEntity<?> part = (COTWPartEntity<?>) this.getParts()[i];
            //Constants.LOG.info("Set id for part {} of {} to {}", part.getPartName(), this.getName().getString(), part.getId());
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        if(this.craft_of_the_wild$forgeParts == null){
            this.craft_of_the_wild$forgeParts = new PartEntity[this.shadow$getSubEntities().length];
            for(int i = 0; i < this.craft_of_the_wild$forgeParts.length; i++){
                PartEntity<?> subEntity = (PartEntity<?>) this.shadow$getSubEntities()[i];
                this.craft_of_the_wild$forgeParts[i] = subEntity;
            }
        }
        return this.craft_of_the_wild$forgeParts;
    }
}
