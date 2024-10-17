package com.Thelnfamous1.craft_of_the_wild.compat.dynamiclights;

import com.Thelnfamous1.craft_of_the_wild.entity.Beedle;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;

public class COTWDynamicLightHandlers {
    public static final DynamicLightHandler<Beedle> BEEDLE = DynamicLightHandler.makeHandler(
            beedle -> beedle.isLightOn() ? Blocks.TORCH.defaultBlockState().getLightEmission() : 0,
            Entity::isUnderWater);
}
