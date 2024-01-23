package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface COTWPartEntity {
    static <T extends LivingEntity & COTWMultipartEntity> void basicTicker(Entity part, T parent, PartEntityController.PartInfo partInfo){
        float yRot = partInfo.bodyPart() ? parent.yBodyRot : parent.getYRot();
        //float xRot = parent.getXRot();
        Vec3 offsetVec = new Vec3(partInfo.xOffset(), partInfo.yOffset(), partInfo.zOffset())
                //.xRot(-xRot * Mth.DEG_TO_RAD)
                .yRot(-yRot * Mth.DEG_TO_RAD)
                .scale(parent.getScale() * partInfo.scale());
        part.setPos(parent.getX() + offsetVec.x, parent.getY() + offsetVec.y, parent.getZ() + offsetVec.z);
    }

    PartEntityController.PartInfo getInfo();
}
