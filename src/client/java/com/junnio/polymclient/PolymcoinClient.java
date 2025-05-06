package com.junnio.polymclient;

import com.junnio.polycoin.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class PolymcoinClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.POLYM_TABLE_SCREEN_HANDLER, PolymTableScreen::new);
	}
}