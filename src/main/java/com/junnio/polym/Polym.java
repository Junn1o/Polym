package com.junnio.polym;

import com.junnio.polym.block.ModBlocks;
import com.junnio.polym.command.ModCommand;
import com.junnio.polym.item.ModItemGroups;
import com.junnio.polym.item.ModItems;
import com.junnio.polym.net.ModNetwork;
import com.junnio.polym.recipe.ModRecipes;
import com.junnio.polym.screen.ModScreenHandlers;
import com.junnio.polym.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.LoggerFactory;

public class Polym implements ModInitializer {
	public static final String MOD_ID = "polym";

	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModBlocks.initialize();
		ModItemGroups.initialize();
		ModRecipes.initialize();
		ModScreenHandlers.initialize();
		ModSounds.initialize();
		ModCommand.initialize();
		ModNetwork.initialize();
		LoggerFactory.getLogger(MOD_ID).info("Polym Initialized!");
	}
}