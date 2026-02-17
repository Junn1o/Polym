package com.junnio.polymclient.screen;

import com.junnio.polym.net.OpenSellerPayLoad;
import com.junnio.polym.net.OpenShopByOwnerPayload;
import com.junnio.polym.net.OpenShopPayLoad;
import com.junnio.polym.screen.PolymTableScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class PolymTableScreen extends AbstractContainerScreen<PolymTableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");

    public PolymTableScreen(PolymTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        int top = this.topPos + 4;
        int right  = this.leftPos + this.imageWidth;

        this.addRenderableWidget(
                Button.builder(Component.literal("+"), btn -> {
                    ClientPlayNetworking.send(new OpenShopByOwnerPayload(UUID.fromString("a205b8da-efc6-37ad-8e1d-84c0239cdd21")));
                }).bounds(right, top, 16, 16).build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("-"), btn -> {
                    ClientPlayNetworking.send(new OpenSellerPayLoad());
                }).bounds(right, top+20, 16, 16).build()
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,this.leftPos, this.topPos, 0F, 0F, this.imageWidth, this.imageHeight,256,256);
    }
}