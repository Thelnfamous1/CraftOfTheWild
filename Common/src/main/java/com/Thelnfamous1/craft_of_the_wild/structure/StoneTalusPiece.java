package com.Thelnfamous1.craft_of_the_wild.structure;

import com.Thelnfamous1.craft_of_the_wild.entity.StoneTalus;
import com.Thelnfamous1.craft_of_the_wild.init.EntityInit;
import com.Thelnfamous1.craft_of_the_wild.init.WorldGenInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class StoneTalusPiece extends ScatteredFeaturePiece {
    public static final int STONE_TALUS_SPAWN_WIDTH = 7;
    public static final int STONE_TALUS_SPAWN_DEPTH = 7;
    private boolean spawnedTalus;

    public StoneTalusPiece(RandomSource pRandom, int pX, int pZ) {
        super(WorldGenInit.STONE_TALUS_SPT.get(), pX, 64, pZ, STONE_TALUS_SPAWN_WIDTH, 1, STONE_TALUS_SPAWN_DEPTH, getRandomHorizontalDirection(pRandom));
    }

    public StoneTalusPiece(CompoundTag pTag) {
        super(WorldGenInit.STONE_TALUS_SPT.get(), pTag);
        this.spawnedTalus = pTag.getBoolean("StoneTalus");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext pContext, CompoundTag pTag) {
        super.addAdditionalSaveData(pContext, pTag);
        pTag.putBoolean("StoneTalus", this.spawnedTalus);
    }

    @Override
    public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        if (this.updateAverageGroundHeight(worldGenLevel, boundingBox, 0)) {
            if (!this.spawnedTalus) {
                BlockPos blockpos = this.getWorldPos(0, 0, 0);
                if (boundingBox.isInside(blockpos)) {
                    this.spawnedTalus = true;
                    StoneTalus stoneTalus = EntityInit.STONE_TALUS.get().create(worldGenLevel.getLevel());
                    if (stoneTalus != null) {
                        stoneTalus.setPersistenceRequired();
                        stoneTalus.moveTo((double)blockpos.getX() + 0.5D, blockpos.getY(), (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
                        stoneTalus.finalizeSpawn(worldGenLevel, worldGenLevel.getCurrentDifficultyAt(blockpos), MobSpawnType.STRUCTURE, null, null);
                        worldGenLevel.addFreshEntityWithPassengers(stoneTalus);
                    }
                }
            }
        }
    }
}
