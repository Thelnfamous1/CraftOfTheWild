package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SinglePoolElement.class)
public interface SinglePoolElementAccessor {

    @Invoker("getTemplate")
    StructureTemplate craft_of_the_wild$callGetTemplate(StructureTemplateManager structureTemplateManager);
}
