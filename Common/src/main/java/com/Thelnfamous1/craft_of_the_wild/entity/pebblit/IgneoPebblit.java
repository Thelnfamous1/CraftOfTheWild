package com.Thelnfamous1.craft_of_the_wild.entity.pebblit;

import com.Thelnfamous1.craft_of_the_wild.COTWCommon;
import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import com.Thelnfamous1.craft_of_the_wild.util.COTWTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.util.BrainUtils;

public class IgneoPebblit extends StonePebblit{
    public IgneoPebblit(EntityType<? extends IgneoPebblit> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return StonePebblit.createAttributes();
    }

    public static void applyBurnEffect(Entity target) {
        if(!target.fireImmune()){
            int remainingTicksOnFire = Math.max(0, target.getRemainingFireTicks());
            target.setRemainingFireTicks(remainingTicksOnFire + (4 * 20));
            COTWCommon.debug(Constants.DEBUG_IGNEO_TALUS, "Pebblit burned {} for {} ticks and they are {}", target, target.getRemainingFireTicks(), (target.isOnFire() ? "on fire" : "not on fire"));
        }
    }

    @Override
    protected void doPostDamageEffects(Entity target) {
        applyBurnEffect(target);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(this.tickCount % 20 == 0){
            BrainUtils.withMemory(this, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, nvle -> {
                nvle.find(le ->
                                !le.getItemBySlot(EquipmentSlot.FEET).is(COTWTags.BURN_IMMUNE_WEARABLES)
                                        && COTWSharedAi.isEntityAttackable(this, le, getTargetingRange(this))
                                        && this.isTargetOnTopOfMe(le)
                                        && !le.isOnFire())
                        .forEach(IgneoPebblit::applyBurnEffect);
            });
        }
    }
}
