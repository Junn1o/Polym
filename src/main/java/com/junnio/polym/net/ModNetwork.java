package com.junnio.polym.net;

import com.junnio.polym.Polym;
import com.junnio.polym.screen.ShopScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.Nullable;

public class ModNetwork {
    public static final Identifier OPEN_SHOP = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "open_shop");

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(OpenShopPayLoad.TYPE, OpenShopPayLoad.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenShopPayLoad.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                var player = context.player();
                player.openMenu(new MenuProvider() {
                    @Override
                    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return new ShopScreenHandler(i, inventory, player);
                    }

                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Shop");
                    }
                });
            });
        });
    }
}
