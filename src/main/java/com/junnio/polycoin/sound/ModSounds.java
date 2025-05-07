package com.junnio.polycoin.sound;

import com.junnio.polycoin.PoLymCoin;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final BlockSoundGroup POLYM_TABLE_SOUND_GROUP = new BlockSoundGroup(
            1.0F, 1.0F,
            SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundEvents.BLOCK_GLASS_STEP, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundEvents.BLOCK_BEACON_AMBIENT, SoundEvents.BLOCK_GLASS_FALL);
    public static void initialize(){

    }
}
