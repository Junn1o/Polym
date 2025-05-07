package com.junnio.polym.sound;

import com.junnio.polym.Polym;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent POLYM_ON_CRAFT = register("kaching");
    public static final BlockSoundGroup POLYM_TABLE_SOUND_GROUP = new BlockSoundGroup(
            1.0F, 1.0F,
            SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK,
            SoundEvents.BLOCK_AMETHYST_BLOCK_STEP,
            SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE,
            SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,
            SoundEvents.BLOCK_AMETHYST_BLOCK_FALL);
    private static SoundEvent register(String name) {
        Identifier id = Identifier.of(Polym.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    public static void initialize(){

    }
}
