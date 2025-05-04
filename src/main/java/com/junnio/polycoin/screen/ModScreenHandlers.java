package com.junnio.polycoin.screen;

import com.junnio.polycoin.PoLymCoin;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<PolymTableScreenHandler> TACKLEBOX_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoLymCoin.MOD_ID, "polym_screen_handler"),
                    new ExtendedScreenHandlerType<>(PolymTableScreenHandler::new, BlockPos.PACKET_CODEC));


    public static void initialize() {
        PoLymCoin.LOGGER.info("Registering Screen Handlers for " + PoLymCoin.MOD_ID);
    }
}
