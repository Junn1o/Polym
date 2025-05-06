package com.junnio.polycoin;

import com.junnio.polycoin.block.ModBlocks;
import com.junnio.polycoin.item.ModItemGroups;
import com.junnio.polycoin.item.ModItems;
import com.junnio.polycoin.recipe.ModRecipes;
import com.junnio.polycoin.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoLymCoin implements ModInitializer {
	public static final String MOD_ID = "polymcoin";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModBlocks.initialize();
		ModItemGroups.initialize();
		ModRecipes.initialize();
		//ModBlockEntities.initialize();
		ModScreenHandlers.initialize();
	}
}