package com.Thelnfamous1.craft_of_the_wild.mixin;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(Path.class)
public interface PathAccessor {

    @Invoker("setDebug")
    void callSetDebug(Node[] openSet, Node[] closedSet, Set<Target> targets);

    @Accessor("targetNodes")
    @Nullable
    Set<Target> getTargetNodes();
}
