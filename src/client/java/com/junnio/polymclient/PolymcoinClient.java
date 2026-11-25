package com.junnio.polymclient;

import com.junnio.polym.block.ModBlocks;
import com.junnio.polym.net.seller.SellerOffersSyncPayload;
import com.junnio.polymclient.screen.ModScreen;
import com.junnio.polymclient.screen.SellerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if >=1.21.6 {
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
//?} else {
/*import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
*///?}

public class PolymcoinClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModScreen.init();
		//? if >=1.21.6 {
		BlockRenderLayerMap.putBlock(ModBlocks.POLYM_TABLE, ChunkSectionLayer.CUTOUT);
		//?} else
		/*BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POLYM_TABLE, RenderType.cutout());*/
		ClientPlayNetworking.registerGlobalReceiver(SellerOffersSyncPayload.TYPE, (payload, context) -> {
			context.client().execute(() -> {
				if (context.client().screen instanceof SellerScreen screen) {
					screen.setOffersFromServer(payload.offers());
				}
			});
		});
	}
}