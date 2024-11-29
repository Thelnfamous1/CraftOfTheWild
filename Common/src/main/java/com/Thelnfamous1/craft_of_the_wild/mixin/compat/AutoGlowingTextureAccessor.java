package com.Thelnfamous1.craft_of_the_wild.mixin.compat;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;

import java.util.function.Function;

@Mixin(value = AutoGlowingTexture.class, remap = false)
public interface AutoGlowingTextureAccessor {

    @Accessor("RENDER_TYPE_FUNCTION")
    static Function<ResourceLocation, RenderType> craftofthewild$getRENDER_TYPE_FUNCTION(){
        throw new AssertionError("AutoGlowingTexture mixin not applied!");
    }
}
