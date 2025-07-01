package com.junnio.polym.screen;

import com.junnio.polym.Polym;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers{
    public static final ScreenHandlerType<PolymTableScreenHandler> POLYM_TABLE_SCREEN_HANDLER =
            new ScreenHandlerType<>(
                    (syncId, inventory) -> new PolymTableScreenHandler(syncId, inventory, inventory.player),
                    FeatureSet.empty()
            );

    public static void initialize() {
        Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Polym.MOD_ID, "polym_table"), POLYM_TABLE_SCREEN_HANDLER);
    }
}