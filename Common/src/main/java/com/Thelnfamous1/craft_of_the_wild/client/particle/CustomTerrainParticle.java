package com.Thelnfamous1.craft_of_the_wild.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CustomTerrainParticle extends TextureSheetParticle {
    private final BlockPos pos;
    private final float uo;
    private final float vo;

    public CustomTerrainParticle(
        ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, BlockState pState
    ) {
        this(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pState, BlockPos.containing(pX, pY, pZ));
    }

    public CustomTerrainParticle(
        ClientLevel pLevel,
        double pX,
        double pY,
        double pZ,
        double pXSpeed,
        double pYSpeed,
        double pZSpeed,
        BlockState pState,
        BlockPos pPos
    ) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.pos = pPos;
        this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(pState));
        this.gravity = 1.0F;
        this.rCol = 0.6F;
        this.gCol = 0.6F;
        this.bCol = 0.6F;
        if (/*IClientBlockExtensions.of(pState).areBreakingParticlesTinted(pState, pLevel, pPos)*/ !pState.is(Blocks.GRASS_BLOCK)) {
            int i = Minecraft.getInstance().getBlockColors().getColor(pState, pLevel, pPos, 0);
            this.rCol *= (float)(i >> 16 & 0xFF) / 255.0F;
            this.gCol *= (float)(i >> 8 & 0xFF) / 255.0F;
            this.bCol *= (float)(i & 0xFF) / 255.0F;
        }

        this.quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0F) / 4.0F);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0F);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0F);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / 4.0F);
    }

    @Override
    public int getLightColor(float pPartialTick) {
        int i = super.getLightColor(pPartialTick);
        return i == 0 && this.level.hasChunkAt(this.pos) ? LevelRenderer.getLightColor(this.level, this.pos) : i;
    }

    @Nullable
    static CustomTerrainParticle createTerrainParticle(
        BlockParticleOption pType,
        ClientLevel pLevel,
        double pX,
        double pY,
        double pZ,
        double pXSpeed,
        double pYSpeed,
        double pZSpeed
    ) {
        BlockState blockstate = pType.getState();
        return !blockstate.isAir() && !blockstate.is(Blocks.MOVING_PISTON) && /*blockstate.shouldSpawnTerrainParticles()*/ true
            ? new CustomTerrainParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, blockstate).updateSprite(blockstate, BlockPos.ZERO/*pType.getPos()*/)
            : null;
    }

    public CustomTerrainParticle updateSprite(BlockState state, BlockPos pos) { //FORGE: we cannot assume that the x y z of the particles match the block pos of the block.
        if (pos != null) // There are cases where we are not able to obtain the correct source pos, and need to fallback to the non-model data version
            this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state)/*.getTexture(state, level, pos)*/);
        return this;
    }

    public static class DustPillarProvider implements ParticleProvider<BlockParticleOption> {
        @Nullable
        public Particle createParticle(
            BlockParticleOption pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            Particle particle = CustomTerrainParticle.createTerrainParticle(pType, pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            if (particle != null) {
                particle.setParticleSpeed(
                    pLevel.random.nextGaussian() / 30.0, pYSpeed + pLevel.random.nextGaussian() / 2.0, pLevel.random.nextGaussian() / 30.0
                );
                particle.setLifetime(pLevel.random.nextInt(20) + 20);
            }

            return particle;
        }
    }

    public static class Provider implements ParticleProvider<BlockParticleOption> {
        @Nullable
        public Particle createParticle(
            BlockParticleOption pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            return CustomTerrainParticle.createTerrainParticle(pType, pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}