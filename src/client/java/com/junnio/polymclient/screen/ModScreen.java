package com.junnio.polymclient.screen;

import com.junnio.polym.screen.ModScreenHandlers;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModScreen {
    public static void init(){
        MenuScreens.register(ModScreenHandlers.POLYM_TABLE_SCREEN_HANDLER, PolymTableScreen::new);
        MenuScreens.register(ModScreenHandlers.SHOP_SCREEN_HANDLER, ShopScreen::new);
        MenuScreens.register(ModScreenHandlers.SELLER_SCREEN_HANDLER, SellerScreen::new);
    }
}
