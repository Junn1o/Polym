package com.junnio.polymclient;

import com.junnio.polym.block.ModBlocks;
import com.junnio.polym.screen.ModScreenHandlers;
import com.junnio.polymclient.screen.ModScreen;
import com.junnio.polymclient.screen.PolymTableScreen;
import com.junnio.polymclient.screen.ShopScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class PolymcoinClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModScreen.init();
		BlockRenderLayerMap.putBlock(ModBlocks.POLYM_TABLE, ChunkSectionLayer.CUTOUT);
	}
}