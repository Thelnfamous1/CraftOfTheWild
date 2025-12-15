package com.Thelnfamous1.craft_of_the_wild.entity.pebblit;

import com.Thelnfamous1.craft_of_the_wild.duck.FreezeAttackVictim;
import com.Thelnfamous1.craft_of_the_wild.entity.ai.COTWSharedAi;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.util.BrainUtils;

public class FrostPebblit extends StonePebblit{
    public FrostPebblit(EntityType<? extends FrostPebblit> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return StonePebblit.createAttributes();
    }

    public static void applyFreezeEffect(Entity target) {
        if(target.canFreeze()){
            int ticksRequiredToFreeze = target.getTicksRequiredToFreeze();
            int frozenTicksToAdd = (int) (ticksRequiredToFreeze / 2.0D);
            frozenTicksToAdd = Math.min(ticksRequiredToFreeze - target.getTicksFrozen(), frozenTicksToAdd);
            FreezeAttackVictim.applyFreezeEffect(target, frozenTicksToAdd, 160);
        }
    }

    @Override
    protected void doPostDamageEffects(Entity target) {
        applyFreezeEffect(target);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(this.tickCount % 20 == 0){
            BrainUtils.withMemory(this, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, nvle -> {
                nvle.find(le ->
                                !le.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FREEZE_IMMUNE_WEARABLES)
                                        && COTWSharedAi.isEntityAttackable(this, le, getTargetingRange(this))
                                        && this.isTargetOnTopOfMe(le)
                                        && !le.isFullyFrozen())
                        .forEach(FrostPebblit::applyFreezeEffect);
            });
        }
    }
}
