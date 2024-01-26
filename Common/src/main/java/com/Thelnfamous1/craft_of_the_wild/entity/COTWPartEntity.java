package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface COTWPartEntity {

    static <T extends LivingEntity & COTWMultipartEntity> void basicTicker(Entity part, T parent, PartEntityController.PartInfo partInfo){
        basicTicker(part, parent, partInfo, false);
    }
    static <T extends LivingEntity & COTWMultipartEntity> void basicTicker(Entity part, T parent, PartEntityController.PartInfo partInfo, boolean applyXRot){
        Vec3 offsetVec = new Vec3(partInfo.xOffset(), partInfo.yOffset(), partInfo.zOffset())
                .scale(parent.getScale() * partInfo.scale());
        if(applyXRot){
            float xRot = parent.getXRot();
            offsetVec = offsetVec
                    .xRot(-xRot * Mth.DEG_TO_RAD);
        }
        float yRot = partInfo.bodyPart() ? parent.yBodyRot : parent.getYRot();
        offsetVec = offsetVec
                .yRot(-yRot * Mth.DEG_TO_RAD);
        part.setPos(
                parent.getX() + offsetVec.x,
                parent.getY() + offsetVec.y,
                parent.getZ() + offsetVec.z);
    }

    PartEntityController.PartInfo getInfo();
}
