package com.Thelnfamous1.craft_of_the_wild.entity.ai;

import com.Thelnfamous1.craft_of_the_wild.util.COTWUtil;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

public class COTWSharedAi {
    public static ExtendedBehaviour<Mob> createVanillaStyleLookAtTarget() {
        return new LookAtTarget<>()
                .stopIf(mob -> COTWUtil.getOptionalMemory(mob, MemoryModuleType.LOOK_TARGET)
                        .filter(pt -> pt.isVisibleBy(mob))
                        .isEmpty())
                .whenStopping(talus -> BrainUtils.clearMemory(talus, MemoryModuleType.LOOK_TARGET))
                .runFor(talus -> talus.getRandom().nextIntBetweenInclusive(45, 90));
    }
}
