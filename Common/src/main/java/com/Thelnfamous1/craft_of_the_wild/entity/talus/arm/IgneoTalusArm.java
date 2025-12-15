package com.Thelnfamous1.craft_of_the_wild.entity.talus.arm;

import com.Thelnfamous1.craft_of_the_wild.entity.talus.IgneoTalus;
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

    @Override
    protected void doPostDamageEffects(LivingEntity target) {
        IgneoTalus.applyBurnEffect(target);
    }

    @Override
    protected void spawnServerSideAOEParticles(BlockPos blockPos, Vec3 particlePos) {
        Services.PLATFORM.sendCircleParticlesPacket(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, this.getRadius(), 750);
    }

    @Override
    protected SoundEvent getImpactSound() {
        return SoundInit.IGNEO_TALUS_BREAK_ROCKS.get();
    }
}
