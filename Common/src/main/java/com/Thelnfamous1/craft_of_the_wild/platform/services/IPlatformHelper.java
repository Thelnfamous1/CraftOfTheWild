package com.Thelnfamous1.craft_of_the_wild.platform.services;

import com.Thelnfamous1.craft_of_the_wild.entity.MultipartEntity;
import com.Thelnfamous1.craft_of_the_wild.entity.PartEntityController;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <P extends LivingEntity & MultipartEntity> PartEntityController<? extends Entity> makePartEntityController(P parent, PartEntityController.Info... infos);

    boolean canEntityGrief(Level level, Entity entity);
}