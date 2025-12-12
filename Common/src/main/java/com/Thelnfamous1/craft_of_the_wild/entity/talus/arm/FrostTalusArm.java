package com.Thelnfamous1.craft_of_the_wild.entity.talus.arm;

import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FrostTalusArm extends StoneTalusArm{
    public FrostTalusArm(EntityType<? extends StoneTalusArm> type, Level level) {
        super(type, level);
    }

    public FrostTalusArm(Level level, LivingEntity shooter, double xDist, double yDist, double zDist) {
        super(EntityInit.FROST_TALUS_ARM.get(), level, shooter, xDist, yDist, zDist);
    }

    public FrostTalusArm(Level level, double x, double y, double z, double xDist, double yDist, double zDist) {
        super(EntityInit.FROST_TALUS_ARM.get(), level, x, y, z, xDist, yDist, zDist);
    }
}
