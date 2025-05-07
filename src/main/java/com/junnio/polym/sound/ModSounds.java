package com.junnio.polym.sound;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class ModSounds {

    public static final BlockSoundGroup POLYM_TABLE_SOUND_GROUP = new BlockSoundGroup(
            1.0F, 1.0F,
            SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK,
            SoundEvents.BLOCK_AMETHYST_BLOCK_STEP,
            SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE,
            SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,
            SoundEvents.BLOCK_AMETHYST_BLOCK_FALL);
    public static void initialize(){

    }
}
