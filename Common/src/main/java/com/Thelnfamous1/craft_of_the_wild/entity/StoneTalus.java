package com.Thelnfamous1.craft_of_the_wild.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.WalkOrRunToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class StoneTalus extends Monster implements GeoEntity, SmartBrainOwner<StoneTalus>, AnimatedAttacker<StoneTalusAttackType> {
    private static final EntityDataAccessor<Byte> DATA_ATTACK_TYPE_ID = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Long> DATA_LAST_ATTACK_TICK = SynchedEntityData.defineId(StoneTalus.class, EntityDataSerializers.LONG);


    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private StoneTalusAttackType currentAttackType;

    public StoneTalus(EntityType<? extends StoneTalus> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Monster.createMonsterAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACK_TYPE_ID, (byte)StoneTalusAttackType.NONE.getId());
        this.entityData.define(DATA_LAST_ATTACK_TICK, 0L);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide){
            if(this.isAttackAnimationInProgress()){
                if(this.isTimeToAttack()){
                    this.executeAttack();
                }
            } else{
                this.setCurrentAttackType(StoneTalusAttackType.NONE);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if(!this.isAttackAnimationInProgress()){
            this.setLastAttackTick(this.level().getGameTime());
        }
        return false;
    }

    protected void executeAttack(){
        this.level().broadcastEntityEvent(this, AnimatedAttacker.ATTACK_EVENT_ID);
        //this.playSound(this.getAttackSound(), 1.0F, 1.0F);
        AABB attackBox = this.createAttackBox();
        List<LivingEntity> targets = this.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, attackBox);
        targets.forEach(this::damageTarget);
    }

    protected AABB createAttackBox() {
        double attackRadius = this.getAttackRadius();
        Vec3 baseOffset = new Vec3(0.0D, 0.0D, this.getBbWidth() * 0.5F).yRot(-this.getYHeadRot() * Mth.DEG_TO_RAD);
        Vec3 attackOffset = new Vec3(0.0D, 0.0D, attackRadius * this.getScale()).yRot(-this.getYHeadRot() * Mth.DEG_TO_RAD);
        double attackSize = attackRadius * 2;
        return AABB.ofSize(this.getBoundingBox().getCenter().add(baseOffset).add(attackOffset), attackSize, attackSize, attackSize);
    }

    protected double getAttackRadius() {
        return 1.5;
    }

    public boolean damageTarget(Entity target){
        return super.doHurtTarget(target);
    }

    @Override
    public long getTicksSinceLastAttack() {
        return this.level().getGameTime() - this.getLastAttackTick();
    }

    @Override
    public long getLastAttackTick() {
        return this.entityData.get(DATA_LAST_ATTACK_TICK);
    }

    @Override
    public void setLastAttackTick(long lastAttackTick) {
        this.entityData.set(DATA_LAST_ATTACK_TICK, lastAttackTick);
    }

    @Override
    public StoneTalusAttackType getCurrentAttackType() {
        return !this.level().isClientSide ? this.currentAttackType : StoneTalusAttackType.byId(this.entityData.get(DATA_ATTACK_TYPE_ID));
    }

    @Override
    public void setCurrentAttackType(StoneTalusAttackType attackType) {
        this.currentAttackType = attackType;
        this.entityData.set(DATA_ATTACK_TYPE_ID, (byte)attackType.getId());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.tickBrain(this);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this, true, false);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(Activity.FIGHT, Activity.IDLE);
    }

    @Override
    public List<? extends ExtendedSensor<? extends StoneTalus>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>()
                        .stopIf(entity -> entity.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter(pt -> pt.isVisibleBy(entity)).isEmpty())
                        .whenStopping(entity -> BrainUtils.clearMemory(entity, MemoryModuleType.LOOK_TARGET))
                        .runFor(entity -> entity.getRandom().nextIntBetweenInclusive(40, 90)),
                new WalkOrRunToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>().speedMod((dino, target) -> 1.0F),
                new AnimatableMeleeAttack<StoneTalus>(0)
                        .attackInterval(attacker -> attacker.getCurrentAttackType().getAttackAnimationLength())
                        .whenStarting(attacker -> attacker.setCurrentAttackType(StoneTalusAttackType.values()[attacker.getRandom().nextInt(StoneTalusAttackType.values().length)]))
        );
    }

    @Override
    public BrainActivityGroup<? extends StoneTalus> getIdleTasks() {
        return BrainActivityGroup.idleTasks(

        );
    }
}
