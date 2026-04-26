package com.Thelnfamous1.craft_of_the_wild.util;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

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

    public static Vec3 xYRotatedZVector(double z, float xRot, float yRot) {
        return new Vec3(0.0D, 0.0D, z)
                .xRot(-xRot * Mth.DEG_TO_RAD)
                .yRot(-yRot * Mth.DEG_TO_RAD);
    }

    public static Vec3 yRotatedXVector(double x, float yRot) {
        return yRotatedXZVector(x, 0, yRot);
    }

    public static double getFollowRange(Mob mob) {
        return mob.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    public static void playVanillaExplosionSound(Entity entity, SoundEvent explosionSound, float volume) {
        entity.playSound(explosionSound, volume, (1.0F + (entity.level().random.nextFloat() - entity.level().random.nextFloat()) * 0.2F) * 0.7F);
    }

    public static void spawnVanillaExplosionParticles(ServerLevel level, double radius, Vec3 posVec) {
        if(radius > 2){
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, posVec.x, posVec.y, posVec.z, 0, 1.0D, 0.0D, 0.0D, 1);
        } else{
            level.sendParticles(ParticleTypes.EXPLOSION, posVec.x, posVec.y, posVec.z, 0, 1.0D, 0.0D, 0.0D, 1);
        }
    }

    public static ObjectArrayList<BlockPos> collectBlocksToDestroy(Level level, double x, double y, double z, float radius){
        ObjectArrayList<BlockPos> toDestroy = new ObjectArrayList<>();
        Set<BlockPos> set = Sets.newHashSet();
        int size = 16;

        for(int xOffset = 0; xOffset < size; ++xOffset) {
            for(int yOffset = 0; yOffset < size; ++yOffset) {
                for(int zOffset = 0; zOffset < size; ++zOffset) {
                    if (xOffset == 0 || xOffset == size-1 || yOffset == 0 || yOffset == size-1 || zOffset == 0 || zOffset == size-1) {
                        double xDist = (float)xOffset / (size-1) * 2.0F - 1.0F;
                        double yDist = (float)yOffset / (size-1) * 2.0F - 1.0F;
                        double zDist = (float)zOffset / (size-1) * 2.0F - 1.0F;
                        double distance = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
                        xDist /= distance;
                        yDist /= distance;
                        zDist /= distance;
                        float breakStrength = radius * (0.7F + level.random.nextFloat() * 0.6F);
                        double targetX = x;
                        double targetY = y;
                        double targetZ = z;

                        for(float step = 0.3F; breakStrength > 0.0F; breakStrength -= 0.22500001F) {
                            BlockPos targetBlockPos = BlockPos.containing(targetX, targetY, targetZ);
                            if (!level.isInWorldBounds(targetBlockPos)) {
                                break;
                            }

                            set.add(targetBlockPos);

                            targetX += xDist * (double)step;
                            targetY += yDist * (double)step;
                            targetZ += zDist * (double)step;
                        }
                    }
                }
            }
        }

        toDestroy.addAll(set);
        return toDestroy;
    }

    public static void destroyBlocks(Level level, @Nullable Entity destroyer, ObjectArrayList<BlockPos> toDestroy, Predicate<BlockState> canBeDestroyed){
        Util.shuffle(toDestroy, level.random);
        for(BlockPos blockPos : toDestroy) {
            BlockState blockState = level.getBlockState(blockPos);
            if (!blockState.isAir() && canBeDestroyed.test(blockState)) {
                level.destroyBlock(blockPos, true, destroyer);
            }
        }
    }

    public static void destroyBlocksInBoundingBox(AABB searchBox, Level level, @Nullable Entity destroyer, Predicate<BlockState> canDestroy) {
        for(BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(searchBox.minX), Mth.floor(searchBox.minY), Mth.floor(searchBox.minZ), Mth.floor(searchBox.maxX), Mth.floor(searchBox.maxY), Mth.floor(searchBox.maxZ))) {
            BlockState blockState = level.getBlockState(blockPos);
            if (!blockState.isAir() && canDestroy.test(blockState)) {
                level.destroyBlock(blockPos, true, destroyer);
            }
        }
    }

    public static void convertGrassToDirt(AABB searchBox, Level level, @Nullable Entity destroyer, Predicate<BlockState> canTurnIntoDirt) {
        for(BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(searchBox.minX), Mth.floor(searchBox.minY), Mth.floor(searchBox.minZ), Mth.floor(searchBox.maxX), Mth.floor(searchBox.maxY), Mth.floor(searchBox.maxZ))) {
            BlockState blockState = level.getBlockState(blockPos);
            if (!blockState.isAir() && canTurnIntoDirt.test(blockState)) {
                level.setBlockAndUpdate(blockPos, Blocks.DIRT.withPropertiesOf(blockState));
            }
        }
    }

    public static <U> Optional<U> getOptionalMemory(LivingEntity entity, MemoryModuleType<U> memoryType){
        return entity.getBrain().getMemory(memoryType);
    }

    public static float getTickDelta(float totalDelta, float ticks){
        return totalDelta / ticks;
    }

    public static int getSecondsDifferenceInTicks(float start, float end){
        return Mth.abs(secondsToTicks(end) - secondsToTicks(start));
    }

    public static boolean isInRange(double value, double min, double max) {
        if(min > max){
            throw new IllegalArgumentException("Min cannot be greater than max!");
        }
        return value >= min && value <= max;
    }

    public static void spawnParticlesInCircle(LevelAccessor world, ParticleOptions particleType, double x, double y, double z, double xzRadius, int amount) {
        int counter = 0;
        while (counter < amount) {
            double targetX = x + Math.cos((Mth.TWO_PI / amount) * counter) * xzRadius;
            double targetZ = z + Math.sin((Mth.TWO_PI / amount) * counter) * xzRadius;
            Vec3 step = new Vec3(targetX - x, 0, targetZ - z).normalize().scale(0.1);
            world.addParticle(particleType,
                    x,
                    y,
                    z,
                    step.x, 0.01, step.z);
            counter = counter + 1;
        }
    }

    public static void setAttackTarget(LivingEntity attacker, @Nullable LivingEntity target){
        if (target == null) {
            BrainUtils.clearMemory(attacker, MemoryModuleType.ATTACK_TARGET);
        } else {
            BrainUtils.setMemory(attacker, MemoryModuleType.ATTACK_TARGET, target);
        }
    }

    public static void stopWalking(Mob mob) {
        BrainUtils.clearMemory(mob, MemoryModuleType.WALK_TARGET);
        mob.getNavigation().stop();
    }

    public static void spawnSmashAttackParticles(LevelAccessor level, AABB attackBounds, int power, BlockParticleOption particleOption) {
        Vec3 boundsBottomCenter = attackBounds.getCenter().subtract(0, attackBounds.getYsize() / 2, 0);
        double xzSize = getXZSize(attackBounds);
        Vec3 particleCenter = boundsBottomCenter.add(0.0, 0.5, 0.0);
        spawnSmashAttackParticles(level, particleOption, particleCenter, xzSize, power);
    }

    public static void spawnSmashAttackParticles(LevelAccessor level, ParticleOptions particleOption, Vec3 particleCenter, double xzSize, int power) {
        double zSpeed;
        int index;
        double x;
        double y;
        double z;
        double xSpeed;
        double ySpeed;
        for(index = 0; (float)index < power / xzSize; ++index) {
            x = particleCenter.x + level.getRandom().nextGaussian() / 2.0;
            y = particleCenter.y;
            z = particleCenter.z + level.getRandom().nextGaussian() / 2.0;
            xSpeed = level.getRandom().nextGaussian() * 0.2;
            ySpeed = level.getRandom().nextGaussian() * 0.2;
            zSpeed = level.getRandom().nextGaussian() * 0.2;
            level.addParticle(particleOption, x, y, z, xSpeed, ySpeed, zSpeed);
        }

        double halfRadius = xzSize * 0.5D;
        for(index = 0; (float)index < power / halfRadius; ++index) {
            x = particleCenter.x + xzSize * Math.cos(index) + level.getRandom().nextGaussian() / 2.0;
            y = particleCenter.y;
            z = particleCenter.z + xzSize * Math.sin(index) + level.getRandom().nextGaussian() / 2.0;
            xSpeed = level.getRandom().nextGaussian() * 0.05;
            ySpeed = level.getRandom().nextGaussian() * 0.05;
            zSpeed = level.getRandom().nextGaussian() * 0.05;
            level.addParticle(particleOption, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    public static double getXZSize(AABB bounds){
        double xSize = bounds.getXsize();
        double zSize = bounds.getZsize();
        return (xSize + zSize) / 2.0;
    }

    public static float getLocalDarknessFactor(float partialTick, Level level, BlockPos pos) {
        float localBrightness = level.getMaxLocalRawBrightness(pos);

        // Compute an ambient darkness factor based on world time
        float timeOfDay = level.getTimeOfDay(partialTick); // 0 to 1 over a full day
        float nightFactor = Mth.cos(timeOfDay * Mth.TWO_PI) * 0.5F + 0.5F;
        // nightFactor is about 0 at midnight, about 1 at noon

        // Darken perceived brightness at night
        float perceivedBrightness = localBrightness * nightFactor;

        return Mth.clamp(1.0F - (perceivedBrightness / 15.0F), 0.0F, 1.0F);
    }

    public static int invertPackedLightByDarkness(int packedLight, float darkness) {
        darkness = Mth.clamp(darkness, 0f, 1f);

        // Extract sky and block light
        int sky = (packedLight >> 20) & 0xF;
        int block = (packedLight >> 4) & 0xF;

        // Invert: move towards full-bright based on darkness
        int newSky = (int) (sky + darkness * (15 - sky));
        int newBlock = (int) (block + darkness * (15 - block));

        // Repack
        return (newSky << 20) | (newBlock << 4);
    }

    @Nullable
    public static Vec3 createExplosionKnockbackVector(Vec3 sourcePos, Entity entity, float diameter){
        double d12 = Math.sqrt(entity.distanceToSqr(sourcePos)) / (double)diameter;
        if (d12 <= 1.0D) {
            double xDist = entity.getX() - sourcePos.x;
            double yDist = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - sourcePos.y;
            double zDist = entity.getZ() - sourcePos.z;
            double dist = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
            if (dist != 0.0D) {
                xDist /= dist;
                yDist /= dist;
                zDist /= dist;
            }
            double seenPercent = Explosion.getSeenPercent(sourcePos, entity);
            double knockbackPower = (1.0D - d12) * seenPercent;
            double knockbackFactor;
            if (entity instanceof LivingEntity livingentity) {
                knockbackFactor = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, knockbackPower);
            } else {
                knockbackFactor = knockbackPower;
            }

            xDist *= knockbackFactor;
            yDist *= knockbackFactor;
            zDist *= knockbackFactor;
            return new Vec3(xDist, yDist, zDist);
        }
        return null;
    }

    public static Set<TagKey<Fluid>> updateAndGetFluidOnEyes(LivingEntity entity, Set<TagKey<Fluid>> fluidsInEye){
        fluidsInEye.clear();
        double eyeYThreshold = entity.getEyeY() - 0.1111111119389534;
        Entity vehicle = entity.getVehicle();
        if (vehicle instanceof Boat boat) {
            if (!boat.isUnderWater() && boat.getBoundingBox().maxY >= eyeYThreshold && boat.getBoundingBox().minY <= eyeYThreshold) {
                return fluidsInEye;
            }
        }

        BlockPos blockPos = BlockPos.containing(entity.getX(), eyeYThreshold, entity.getZ());
        FluidState fluidState = entity.level().getFluidState(blockPos);
        double fluidY = (float)blockPos.getY() + fluidState.getHeight(entity.level(), blockPos);
        if (fluidY > eyeYThreshold) {
            fluidState.getTags().forEach(fluidsInEye::add);
        }
        return fluidsInEye;
    }

    public static double calculateAttributeModifierValue(double baseValue, Attribute attribute, Collection<AttributeModifier> modifiers){
        Collection<AttributeModifier> additions = modifiers.stream().filter(mod -> mod.getOperation() == AttributeModifier.Operation.ADDITION).toList();
        Collection<AttributeModifier> multiplyBases = modifiers.stream().filter(mod -> mod.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE).toList();
        Collection<AttributeModifier> multiplyTotals = modifiers.stream().filter(mod -> mod.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL).toList();

        for(AttributeModifier attributemodifier : additions) {
            baseValue += attributemodifier.getAmount();
        }

        double d1 = baseValue;

        for(AttributeModifier attributemodifier1 : multiplyBases) {
            d1 += baseValue * attributemodifier1.getAmount();
        }

        for(AttributeModifier attributemodifier2 : multiplyTotals) {
            d1 *= 1.0D + attributemodifier2.getAmount();
        }

        return attribute.sanitizeValue(d1);
    }
}
