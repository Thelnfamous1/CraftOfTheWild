package com.Thelnfamous1.craft_of_the_wild.feature;

import com.Thelnfamous1.craft_of_the_wild.entity.ai.sensor.NearestBurrowSensor;
import com.Thelnfamous1.craft_of_the_wild.init.WorldGenInit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class BurrowPlacement extends PlacementFilter {
    public static final Codec<BurrowPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(BurrowPlacement::getEntity)).
            apply(instance, BurrowPlacement::new));

    private final EntityType<?> entity;

    public BurrowPlacement(EntityType<?> entity){
        this.entity = entity;
    }

    public EntityType<?> getEntity() {
        return this.entity;
    }

    @Override
    public PlacementModifierType<?> type() {
        return WorldGenInit.BURROW.get();
    }

    @Override
    protected boolean shouldPlace(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos) {
        return NearestBurrowSensor.canBurrowInto(placementContext.getLevel(), blockPos.below(), this.entity.getWidth(), this.entity.getHeight());
    }
}
