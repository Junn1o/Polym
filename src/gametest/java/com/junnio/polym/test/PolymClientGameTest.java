package com.junnio.polym.test;

import com.junnio.polym.net.seller.OpenShopByOwnerPayload;
import com.junnio.polym.net.shop.OpenAllShopsPayload;
import com.junnio.polym.screen.PolymTableScreenHandler;
import com.junnio.polymclient.screen.PolymTableScreen;
import com.junnio.polymclient.screen.SellerScreen;
import com.junnio.polymclient.screen.ShopScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

import java.util.UUID;

// smoke test: every screen opens and renders, coin glow renders; screenshots land in run/screenshots
public class PolymClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getClientWorld().waitForChunksRender();
            world.getServer().runCommand("time set day");
            world.getServer().runCommand("gamerule doDaylightCycle false");

            world.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new PolymTableScreenHandler(id, inv, p), Component.literal("Polym Table")));
            });
            context.waitForScreen(PolymTableScreen.class);
            context.waitTicks(5);
            context.takeScreenshot(TestScreenshotOptions.of("polym_table"));
            context.setScreen(() -> null);

            context.runOnClient(mc -> ClientPlayNetworking.send(new OpenAllShopsPayload()));
            context.waitForScreen(ShopScreen.class);
            context.waitTicks(5);
            context.takeScreenshot(TestScreenshotOptions.of("shop"));
            context.setScreen(() -> null);

            UUID self = context.computeOnClient(mc -> mc.player.getUUID());
            context.runOnClient(mc -> ClientPlayNetworking.send(new OpenShopByOwnerPayload(self)));
            context.waitForScreen(SellerScreen.class);
            context.waitTicks(5);
            context.takeScreenshot(TestScreenshotOptions.of("seller"));
            context.setScreen(() -> null);

            world.getServer().runCommand("execute as @a at @s run summon item ^ ^1 ^3 {Item:{id:\"polym:gold_coin\",count:1},PickupDelay:32767,NoGravity:1b}");
            context.waitTicks(20);
            context.takeScreenshot(TestScreenshotOptions.of("coin_glow"));
        }
    }
}
