package com.junnio.polymclient;

import com.junnio.polym.block.ModBlocks;
import com.junnio.polym.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;

public class PolymcoinClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.POLYM_TABLE_SCREEN_HANDLER, PolymTableScreen::new);
		BlockRenderLayerMap.putBlock(ModBlocks.POLYM_TABLE, BlockRenderLayer.CUTOUT);

	}
}