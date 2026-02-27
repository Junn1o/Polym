package com.junnio.polym.sound;

import com.junnio.polym.Polym;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class ModSounds {

    public static final SoundEvent POLYM_ON_CRAFT = register("kaching");
    public static final SoundType POLYM_TABLE_SOUND_GROUP = new SoundType(
            1.0F, 1.0F,
            SoundEvents.NETHERITE_BLOCK_BREAK,
            SoundEvents.AMETHYST_BLOCK_STEP,
            SoundEvents.NETHERITE_BLOCK_PLACE,
            SoundEvents.AMETHYST_BLOCK_HIT,
            SoundEvents.AMETHYST_BLOCK_FALL);
    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Polym.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
    public static void initialize(){

    }
}
