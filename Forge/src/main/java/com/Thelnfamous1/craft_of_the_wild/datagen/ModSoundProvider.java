package com.Thelnfamous1.craft_of_the_wild.datagen;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.Thelnfamous1.craft_of_the_wild.init.SoundInit;
import com.nyfaria.craft_of_the_wild.registration.RegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class ModSoundProvider extends SoundDefinitionsProvider {
    public ModSoundProvider(PackOutput generator, ExistingFileHelper helper) {
        super(generator, Constants.MODID, helper);
    }

    @Override
    public void registerSounds() {
        // SoundInit.SOUNDS.getEntries().forEach(this::addSound);
        this.addSound(SoundInit.STONE_TALUS_BOSS_MUSIC);
        this.addSound(SoundInit.STONE_TALUS_DEATH, 2);
        this.addSound(SoundInit.STONE_TALUS_HEADBUTT, 2);
        this.addSound(SoundInit.STONE_TALUS_HURT, 2);
        this.addSound(SoundInit.STONE_TALUS_POUND, 2);
        this.addSound(SoundInit.STONE_TALUS_SPAWN, 2);
        this.addSound(SoundInit.STONE_TALUS_REGENERATE_ARMS, 2);
        this.addSound(SoundInit.STONE_TALUS_THROW_ARMS, 2);
        this.addSound(SoundInit.STONE_TALUS_WALK, 2);
        this.addSound(SoundInit.STONE_TALUS_STUN, 2);
        this.addSound(SoundInit.STONE_TALUS_PUNCH, 2);
        this.addSound(SoundInit.STONE_TALUS_SHAKE, 2);
        this.addSound(SoundInit.STONE_TALUS_BREAK_ROCKS, 2);
    }

    public void addSound(RegistryObject<SoundEvent> entry, float volume) {
        add(entry, SoundDefinition.definition().with(sound(entry.getId()).volume(volume)));
    }

    public void addSound(RegistryObject<SoundEvent> entry) {
        add(entry, SoundDefinition.definition().with(sound(entry.getId())));
    }
}
