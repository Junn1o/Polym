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
import java.util.UUID;

public class ModNetwork {
    public static final Identifier OPEN_SHOP = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "open_shop");

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(OpenShopPayLoad.TYPE, OpenShopPayLoad.CODEC);
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
        PayloadTypeRegistry.playC2S().register(AddOfferFromSlotsPayload.TYPE, AddOfferFromSlotsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SellerOffersSyncPayload.TYPE, SellerOffersSyncPayload.CODEC);
// CHỈ GIỮ 1 receiver
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
                        .limit(300)
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

                ShopOpenData openData = new ShopOpenData(offers);
                player.openMenu(new ExtendedScreenHandlerFactory<ShopOpenData>() {
                    @Override public ShopOpenData getScreenOpeningData(ServerPlayer p) { return openData; }
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
    }
}
