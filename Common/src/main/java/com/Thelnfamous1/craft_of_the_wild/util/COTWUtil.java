package com.Thelnfamous1.craft_of_the_wild.util;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Optional;

public class COTWUtil {

    public static final Vector3f WHITE = Vec3.fromRGB24(16777215).toVector3f();

    public static Optional<MemoryStatus> getMemoryStatus(Brain<?> brain, MemoryModuleType<?> memory){
        for(MemoryStatus memoryStatus : MemoryStatus.values()){
            if(brain.checkMemory(memory, memoryStatus)){
                return Optional.of(memoryStatus);
            }
        }
        return Optional.empty();
    }

    public static double getHitboxAdjustedDistance(LivingEntity attacker, LivingEntity target, double distance) {
        return attacker.getBbWidth() * 0.5 + target.getBbWidth() * 0.5 + distance;
    }

    //https://gist.github.com/dGr8LookinSparky/bd64a9f5f9deecf61e2c3c1592169c00
    public static double getDistSqrBetweenHitboxes(AABB first, AABB second){
        double[] firstMins = new double[]{first.minX, first.minY, first.minZ};
        double[] secondMins = new double[]{second.minX, second.minY, second.minZ};
        double[] firstMaxs = new double[]{first.maxX, first.maxY, first.maxZ};
        double[] secondMaxs = new double[]{second.maxX, second.maxY, second.maxZ};
        float distSqr = 0;
        for(int i = 0; i < 3; i++){
            if(secondMaxs[i] < firstMins[i]) {
                double dist = secondMaxs[i] - firstMins[i];
                distSqr += Mth.square(dist);
            } else if(secondMins[i] > firstMaxs[i]) {
                double dist = secondMins[i] - firstMaxs[i];
                distSqr += Mth.square(dist);
            }
        }
        return distSqr;
    }

    public static double getDistSqrBetweenHitboxes(Entity first, Entity second){
        return getDistSqrBetweenHitboxes(first.getBoundingBox(), second.getBoundingBox());
    }

    public static int secondsToTicks(float seconds){
        return Mth.ceil(seconds * 20);
    }

    public static HolderLookup.Provider createLookup(RegistrySetBuilder builder) {
        RegistryAccess.Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        return builder.build(access);
    }

    public static void sendHitboxParticles(AABB attackBox, Level level) {
        if(level instanceof ServerLevel serverLevel) {
            DustParticleOptions particle = new DustParticleOptions(WHITE, 2.0F);
            serverLevel.sendParticles(particle, attackBox.minX, attackBox.minY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.maxX, attackBox.minY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.minX, attackBox.minY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.maxX, attackBox.minY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.minX, attackBox.maxY, attackBox.minZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.maxX, attackBox.maxY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.minX, attackBox.maxY, attackBox.maxZ, 0, 0, 0, 0, 1);
            serverLevel.sendParticles(particle, attackBox.maxX, attackBox.maxY, attackBox.minZ, 0, 0, 0, 0, 1);
        }
    }

    public static void debugMemoryStatus(boolean debugFlag, LivingEntity entity, MemoryModuleType<?> memoryType) {
        COTWCommon.debug(debugFlag, "{} has {} memory status: {}", entity,
                BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(memoryType), getMemoryStatus(entity.getBrain(), memoryType)
                        .map(Objects::toString)
                        .orElse("UNREGISTERED"));
    }

    public static void readBrainFromTag(CompoundTag saveTag, LivingEntity entity) {
        if(saveTag.contains("Brain", Tag.TAG_COMPOUND)){
            CompoundTag brainTag = saveTag.getCompound("Brain");
            if(brainTag.contains("memories", Tag.TAG_COMPOUND)){
                COTWCommon.debug(Constants.DEBUG_BRAIN_DESERIALIZATION, "Reading in NBT brain memories for {}", entity);
                CompoundTag memoriesTag = brainTag.getCompound("memories");
                COTWCommon.debug(Constants.DEBUG_BRAIN_DESERIALIZATION, "NBT brain memories found for {}: {}", entity, memoriesTag);
                for(MemoryModuleType<?> memoryType : entity.getBrain().getMemories().keySet()){
                    if(memoryType.getCodec().isPresent()){
                        COTWCommon.debug(Constants.DEBUG_BRAIN_DESERIALIZATION, "Reading in {} NBT brain memory for {}", BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(memoryType), entity);
                    }
                    readBrainMemory(memoriesTag, entity, memoryType);
                }
            }
        }
    }

    public static <U> void readBrainMemory(CompoundTag memoriesTag, LivingEntity entity, MemoryModuleType<U> memoryType){
        ResourceLocation key = BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(memoryType);
        if(memoriesTag.contains(key.toString())){
            CompoundTag memoryTag = memoriesTag.getCompound(key.toString());
            memoryType.getCodec()
                    .map(codec -> codec.parse(NbtOps.INSTANCE, memoryTag))
                    .map(data -> data.resultOrPartial(Constants.LOG::error))
                    .ifPresent(memoryValue -> {
                        if(memoryValue.isPresent()){
                            COTWCommon.debug(Constants.DEBUG_BRAIN_DESERIALIZATION, "Set memory value of {} to {} for {}", key, memoryValue.get(), entity);
                            entity.getBrain().setMemoryWithExpiry(memoryType, memoryValue.get().getValue(), memoryValue.get().getTimeToLive());
                        } else{
                            COTWCommon.debug(Constants.DEBUG_BRAIN_DESERIALIZATION, "Erased {} memory for {}", key, entity);
                            entity.getBrain().eraseMemory(memoryType);
                        }
                    });
        }
    }

    public static void saveBrainToTag(CompoundTag saveTag, LivingEntity entity) {
        CompoundTag brainTag = new CompoundTag();
        saveTag.put("Brain", brainTag);
        CompoundTag memoriesTag = new CompoundTag();
        brainTag.put("memories", memoriesTag);
        for(MemoryModuleType<?> memoryType : entity.getBrain().getMemories().keySet()){
            saveBrainMemory(memoriesTag, entity, memoryType);
        }
        COTWCommon.debug(Constants.DEBUG_BRAIN_DESERIALIZATION, "Saved brain for {}: {}", entity, brainTag);
    }

    public static <U> void saveBrainMemory(CompoundTag memoriesTag, LivingEntity entity, MemoryModuleType<U> memoryType){
        memoryType.getCodec()
                .flatMap(codec -> entity.getBrain().getMemories().get(memoryType)
                        .flatMap(memoryValue ->
                                codec.encodeStart(NbtOps.INSTANCE, (ExpirableValue<U>) memoryValue)
                                        .resultOrPartial(Constants.LOG::error)))
                .ifPresent(memoryValueTag ->{
                    ResourceLocation key = BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(memoryType);
                    COTWCommon.debug(Constants.DEBUG_BRAIN_DESERIALIZATION, "Saved memory value {} of {} to NBT for {}", memoryValueTag, key, entity);
                    memoriesTag.put(key.toString(), memoryValueTag);
                });
    }

    public static Vec3 yRotatedXZVector(double x, double z, float yRot) {
        return new Vec3(x, 0.0D, z).yRot(-yRot * Mth.DEG_TO_RAD);
    }

    public static Vec3 yRotatedZVector(double z, float yRot) {
        return yRotatedXZVector(0, z, yRot);
    }

    public static Vec3 yRotatedXVector(double x, float yRot) {
        return yRotatedXZVector(x, 0, yRot);
    }

    public static boolean isLookingAt(LivingEntity looker, Entity target, boolean requireLineOfSight){
        Vec3 viewVec = looker.getViewVector(1.0F).normalize();
        Vec3 distanceVec = new Vec3(target.getX() - looker.getX(), target.getEyeY() - looker.getEyeY(), target.getZ() - looker.getZ());
        double distanceSqr = distanceVec.lengthSqr();
        distanceVec = distanceVec.normalize();
        double dot = viewVec.dot(distanceVec);
        return dot > 1.0 - (Mth.square(0.025) / distanceSqr) && (!requireLineOfSight || looker.hasLineOfSight(target));
    }
}
