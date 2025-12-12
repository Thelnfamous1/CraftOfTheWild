package com.Thelnfamous1.craft_of_the_wild.entity.talus.arm;

import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class IgneoTalusArm extends StoneTalusArm{
    public IgneoTalusArm(EntityType<? extends StoneTalusArm> type, Level level) {
        super(type, level);
    }

    public IgneoTalusArm(Level level, LivingEntity shooter, double xDist, double yDist, double zDist) {
        super(EntityInit.IGNEO_TALUS_ARM.get(), level, shooter, xDist, yDist, zDist);
    }

    public IgneoTalusArm(Level level, double x, double y, double z, double xDist, double yDist, double zDist) {
        super(EntityInit.IGNEO_TALUS_ARM.get(), level, x, y, z, xDist, yDist, zDist);
    }
}
