package com.Thelnfamous1.craft_of_the_wild.block;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.BlockDisguise;
import com.Thelnfamous1.craft_of_the_wild.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StonePebblitBoulderBlockEntity extends BlockEntity {
    private boolean spawned;
    @Nullable
    private TagKey<EntityType<?>> stonePebblitBoulderSpawns;

    public StonePebblitBoulderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public StonePebblitBoulderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockInit.STONE_PEBBLIT_BOULDER_BE.get(), blockPos, blockState);
    }

    static void tickEntity(Level level, BlockPos blockPos, BlockState blockState, StonePebblitBoulderBlockEntity blockEntity) {
        if (!blockEntity.spawned && level.getGameTime() % 20L == 0L && level instanceof ServerLevel serverLevel) {
            blockEntity.spawnPebblit(serverLevel, blockPos, blockState);
        }

    }

    public void setStonePebblitBoulderInfo(TagKey<EntityType<?>> stonePebblitBoulderSpawns){
        this.stonePebblitBoulderSpawns = stonePebblitBoulderSpawns;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("boulder_spawns", CompoundTag.TAG_STRING)){
            ResourceLocation resourcelocation = new ResourceLocation(tag.getString("boulder_spawns"));
            this.stonePebblitBoulderSpawns = TagKey.create(Registries.ENTITY_TYPE, resourcelocation);
        }
        if(tag.contains("spawned")){
            this.spawned = tag.getBoolean("spawned");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(this.stonePebblitBoulderSpawns != null){
            tag.putString("boulder_spawns", this.stonePebblitBoulderSpawns.location().toString());
        }
        tag.putBoolean("spawned", this.spawned);

    }

    public void forceSpawnPebblit(ServerLevel level){
        if(!this.spawned){
            this.spawnPebblit(level, this.getBlockPos(), this.getBlockState());
        }
    }

    private void spawnPebblit(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        Vec3 center = blockPos.getCenter();
        if(serverLevel.getNearestPlayer(center.x, center.y, center.z, 5, true) != null){
            // Spawn the pebblit
            if(this.stonePebblitBoulderSpawns != null){
                Optional<Holder<EntityType<?>>> randomPebblitType = BuiltInRegistries.ENTITY_TYPE.getTag(this.stonePebblitBoulderSpawns).flatMap((entityTypeNamed) -> entityTypeNamed.getRandomElement(serverLevel.getRandom()));
                randomPebblitType.ifPresent(pebblitType -> {
                    COTWCommon.debug(Constants.DEBUG_STONE_TALUS_BOULDER, "Attempting to spawn {} at stone pebblit boulder at {}", pebblitType, blockPos);
                    Entity spawned = pebblitType.value().spawn(serverLevel, null, pebblit -> {
                        if(pebblit instanceof BlockDisguise blockDisguise){
                            blockDisguise.setDisguiseBlockState(blockState);
                        }
                    }, blockPos, MobSpawnType.STRUCTURE, false, false);
                    if(spawned != null){
                        COTWCommon.debug(Constants.DEBUG_STONE_TALUS_BOULDER, "Spawned {} at stone pebblit boulder at {}", spawned, blockPos);
                    }

                });
            }

            // Mark as having destroyed blocks, and remove ourselves (cleanup)
            this.spawned = true;
            serverLevel.removeBlock(blockPos, false);
        }
    }

}
