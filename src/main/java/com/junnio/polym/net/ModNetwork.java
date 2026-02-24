package com.junnio.polym.net;

import com.junnio.polym.Polym;
import com.junnio.polym.net.seller.*;
import com.junnio.polym.net.shop.AllShopOpenData;
import com.junnio.polym.net.shop.OpenAllShopsPayload;
import com.junnio.polym.net.shop.ShopOfferData;
import com.junnio.polym.net.shop.ShopOfferViewData;
import com.junnio.polym.screen.SellerScreenHandler;
import com.junnio.polym.screen.ShopScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class ModNetwork {
    public static final Identifier OPEN_SHOP = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "open_shop");

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(AddOfferFromSlotsPayload.TYPE, AddOfferFromSlotsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SellerOffersSyncPayload.TYPE, SellerOffersSyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AddOfferFromSlotsPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                if (!(player.containerMenu instanceof SellerScreenHandler sh)) return;

                sh.addOfferFromSlots();

                ServerPlayNetworking.send(player, new SellerOffersSyncPayload(sh.getOffers()));
            });
        });
        PayloadTypeRegistry.playC2S().register(SaveSellerOffersPayload.TYPE, SaveSellerOffersPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SaveSellerOffersPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                if (!(player.containerMenu instanceof SellerScreenHandler sh)) return;

                List<ShopOfferData> safe = sh.getOffers().stream()
                        .limit(1000000)
                        .map(o -> new ShopOfferData(
                                o.buyA() == null ? ItemStack.EMPTY : o.buyA(),
                                o.buyB() == null ? ItemStack.EMPTY : o.buyB(),
                                o.sell() == null ? ItemStack.EMPTY : o.sell()
                        ))
                        .toList();

                SellerShopJsonStore store = SellerShopJsonStore.get(context.server());
                store.setShop(player.getUUID(), player.getGameProfile().name(), safe);
                store.saveNow();
            });
        });

        PayloadTypeRegistry.playC2S().register(OpenShopByOwnerPayload.TYPE, OpenShopByOwnerPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OpenShopByOwnerPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                UUID owner = payload.owner();

                SellerShopJsonStore store = SellerShopJsonStore.get(context.server());
                List<ShopOfferData> offers = store.getOffers(owner);

                SellerOfferData openData = new SellerOfferData(offers);
                player.openMenu(new ExtendedScreenHandlerFactory<SellerOfferData>() {
                    @Override public SellerOfferData getScreenOpeningData(ServerPlayer p) { return openData; }
                    @Override public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player p) {
                        return new SellerScreenHandler(syncId, inv, p, openData);
                    }
                    @Override public Component getDisplayName() { return Component.literal("Shop"); }
                });
            });
        });

        PayloadTypeRegistry.playC2S().register(DeleteOfferPayload.TYPE, DeleteOfferPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DeleteOfferPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                if (!(player.containerMenu instanceof SellerScreenHandler sh)) return;

                sh.deleteOffer(payload.index());
                ServerPlayNetworking.send(player, new SellerOffersSyncPayload(sh.getOffers()));
            });
        });
        PayloadTypeRegistry.playC2S().register(EditOfferPayload.TYPE, EditOfferPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EditOfferPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                if (!(player.containerMenu instanceof SellerScreenHandler sh)) return;

                sh.editOfferFromSlotsAndClear(payload.index());
                ServerPlayNetworking.send(player, new SellerOffersSyncPayload(sh.getOffers()));
            });
        });
        PayloadTypeRegistry.playC2S().register(OpenAllShopsPayload.TYPE, OpenAllShopsPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenAllShopsPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                SellerShopJsonStore store = SellerShopJsonStore.get(context.server());

                List<ShopOfferViewData> offers = store.getAllOfferViews();

                AllShopOpenData openData = new AllShopOpenData(offers);
                player.openMenu(new ExtendedScreenHandlerFactory<AllShopOpenData>() {
                    @Override public AllShopOpenData getScreenOpeningData(ServerPlayer p) { return openData; }

                    @Override public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player p) {
                        return new ShopScreenHandler(syncId, inv, p, openData);
                    }

                    @Override public Component getDisplayName() { return Component.literal("All Shops"); }
                });
            });
        });
    }
}
