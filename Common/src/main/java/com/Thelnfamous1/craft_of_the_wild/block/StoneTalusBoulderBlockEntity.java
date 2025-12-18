package com.Thelnfamous1.craft_of_the_wild.block;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.init.BlockInit;
import com.Thelnfamous1.craft_of_the_wild.mixin.SinglePoolElementAccessor;
import com.Thelnfamous1.craft_of_the_wild.mixin.StructureTemplateAccessor;
import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StoneTalusBoulderBlockEntity extends BlockEntity {
    @Nullable
    private ResourceKey<Structure> stoneTalusBoulderStructure;
    @Nullable
    private TagKey<Block> stoneTalusBoulderComponents;
    private boolean destroyedBlocks;

    public StoneTalusBoulderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public StoneTalusBoulderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockInit.STONE_TALUS_BOULDER_BE.get(), blockPos, blockState);
    }

    public void setStoneTalusBoulderInfo(ResourceKey<Structure> structureResourceKey, TagKey<Block> blockTagKey){
        this.stoneTalusBoulderStructure = structureResourceKey;
        this.stoneTalusBoulderComponents = blockTagKey;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("boulder_structure", CompoundTag.TAG_STRING)){
            ResourceLocation resourcelocation = new ResourceLocation(tag.getString("boulder_structure"));
            this.stoneTalusBoulderStructure = ResourceKey.create(Registries.STRUCTURE, resourcelocation);
        }
        if(tag.contains("boulder_components", CompoundTag.TAG_STRING)){
            ResourceLocation resourcelocation = new ResourceLocation(tag.getString("boulder_components"));
            this.stoneTalusBoulderComponents = TagKey.create(Registries.BLOCK, resourcelocation);
        }
        if(tag.contains("destroyed_blocks")){
            this.destroyedBlocks = tag.getBoolean("destroyed_blocks");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(this.stoneTalusBoulderStructure != null){
            tag.putString("boulder_structure", this.stoneTalusBoulderStructure.location().toString());
        }
        if(this.stoneTalusBoulderComponents != null){
            tag.putString("boulder_components", this.stoneTalusBoulderComponents.location().toString());
        }
        tag.putBoolean("destroyed_blocks", this.destroyedBlocks);

    }

    static void tickEntity(Level level, BlockPos blockPos, BlockState blockState, StoneTalusBoulderBlockEntity blockEntity) {
        if(blockEntity.stoneTalusBoulderStructure == null || blockEntity.stoneTalusBoulderComponents == null){
            return;
        }
        if (!blockEntity.destroyedBlocks && level.getGameTime() % 20L == 0L && level instanceof ServerLevel serverLevel) {
            Vec3 center = blockPos.getCenter();
            if(level.getNearestPlayer(center.x, center.y, center.z, 5, true) != null){
                StructureStart boulderStructure = serverLevel.structureManager().getStructureWithPieceAt(blockPos, blockEntity.stoneTalusBoulderStructure);
                if(boulderStructure.isValid() && !boulderStructure.getPieces().isEmpty()){
                    StructurePiece structurePiece = boulderStructure.getPieces().get(0);
                    if(structurePiece instanceof PoolElementStructurePiece poolElementStructurePiece && poolElementStructurePiece.getElement() instanceof SinglePoolElement singlePoolElement){
                        BoundingBox boundingBox = structurePiece.getBoundingBox();
                        COTWCommon.debug(Constants.DEBUG_STONE_TALUS_BOULDER, "{} detected boulder structure with bounds {}", blockEntity, boundingBox);
                        if(Constants.DEBUG_STONE_TALUS_BOULDER){
                            DustParticleOptions particle = new DustParticleOptions(COTWUtil.WHITE, 2.0F);
                            boundingBox.forAllCorners(cornerPos -> {
                                Vec3 cornerCenter = cornerPos.getCenter();
                                serverLevel.sendParticles(particle, cornerCenter.x, cornerCenter.y, cornerCenter.z, 0, 0, 0, 0, 1);
                            });
                        }

                        // Get the template
                        StructureTemplate template = ((SinglePoolElementAccessor)singlePoolElement).craft_of_the_wild$callGetTemplate(serverLevel.getStructureManager());
                        List<StructureTemplate.Palette> palettes = ((StructureTemplateAccessor) template).craft_of_the_wild$getPalettes();
                        StructurePlaceSettings structurePlaceSettings = (new StructurePlaceSettings()).setRotation(structurePiece.getRotation());
                        BlockPos structurePiecePosition = poolElementStructurePiece.getPosition();

                        // Iterate over blocks in the template
                        List<BlockPos> destroyedBlocks = new ArrayList<>();
                        if(!palettes.isEmpty()){
                            StructureTemplate.Palette mainPalette = palettes.get(0);
                            for (StructureTemplate.StructureBlockInfo info : mainPalette.blocks()) {
                                BlockPos worldPos = StructureTemplate.calculateRelativePosition(structurePlaceSettings, info.pos()).offset(structurePiecePosition);
                                BlockState state = info.state();
                                if(state.is(blockEntity.stoneTalusBoulderComponents)){
                                    if(level.getBlockState(worldPos).is(state.getBlock())){
                                        level.destroyBlock(worldPos, false);
                                        destroyedBlocks.add(worldPos);
                                    }
                                }
                            }
                            blockEntity.destroyedBlocks = true;
                            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_BOULDER, "Destroyed {} blocks that comprised the boulder at {}", destroyedBlocks.size(), boundingBox.getCenter());
                        }
                    }
                }
            }
        }

    }
}
