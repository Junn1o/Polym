package com.junnio.polym.net;

import com.junnio.polym.Polym;
import com.junnio.polym.screen.SellerScreenHandler;
import com.junnio.polym.screen.ShopScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ModNetwork {
    public static final Identifier OPEN_SHOP = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "open_shop");

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(OpenShopPayLoad.TYPE, OpenShopPayLoad.CODEC);
        PayloadTypeRegistry.playC2S().register(OpenSellerPayLoad.TYPE, OpenSellerPayLoad.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenShopPayLoad.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                var player = context.player();
                List<ShopOfferData> offers = List.of(
                        new ShopOfferData(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.EMERALD, 5), new ItemStack(Items.DIAMOND, 1))
                );
                ShopOpenData openData = new ShopOpenData(offers);
                player.openMenu(new ExtendedScreenHandlerFactory<ShopOpenData>() {
                    @Override
                    public ShopOpenData getScreenOpeningData(ServerPlayer player) {
                        return openData;
                    }

                    @Override
                    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return new ShopScreenHandler(i, inventory, player, openData);
                    }

                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Shop");
                    }
                });
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(OpenSellerPayLoad.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                var player = context.player();
                List<ShopOfferData> offers = List.of(
                        new ShopOfferData(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.EMERALD, 5), new ItemStack(Items.DIAMOND, 1))
                );
                ShopOpenData openData = new ShopOpenData(offers);
                player.openMenu(new ExtendedScreenHandlerFactory<ShopOpenData>() {
                    @Override
                    public ShopOpenData getScreenOpeningData(ServerPlayer player) {
                        return openData;
                    }

                    @Override
                    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return new SellerScreenHandler(i, inventory, player, openData);
                    }

                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Shop");
                    }
                });
            });
        });
        PayloadTypeRegistry.playC2S().register(AddOfferFromSlotsPayload.TYPE, AddOfferFromSlotsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SellerOffersSyncPayload.TYPE, SellerOffersSyncPayload.CODEC);
// CHỈ GIỮ 1 receiver
        ServerPlayNetworking.registerGlobalReceiver(AddOfferFromSlotsPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                if (!(player.containerMenu instanceof SellerScreenHandler sh)) return;

                sh.addOfferFromSlotsAndClear();

                ServerPlayNetworking.send(player, new SellerOffersSyncPayload(sh.getOffers()));
            });
        });
    }
}
