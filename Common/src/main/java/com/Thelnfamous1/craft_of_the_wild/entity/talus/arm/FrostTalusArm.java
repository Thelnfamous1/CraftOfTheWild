package com.Thelnfamous1.craft_of_the_wild.entity.talus.arm;

import com.Thelnfamous1.craft_of_the_wild.entity.talus.FrostTalus;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.Thelnfamous1.craft_of_the_wild.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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

    @Override
    protected void doPostDamageEffects(LivingEntity target) {
        FrostTalus.applyFreezeEffect(target);
    }

    @Override
    protected void spawnServerSideAOEParticles(BlockPos blockPos, Vec3 particlePos) {
        Services.PLATFORM.sendCircleParticlesPacket(ParticleTypes.SNOWFLAKE, particlePos.x, particlePos.y, particlePos.z, this.getRadius(), 750);
    }

    @Override
    protected SoundEvent getImpactSound() {
        return SoundInit.FROST_TALUS_BREAK_ROCKS.get();
    }
}
