package com.junnio.polymclient.screen;

import com.junnio.polym.Polym;
import com.junnio.polym.net.AddOfferFromSlotsPayload;
import com.junnio.polym.net.SaveSellerOffersPayload;
import com.junnio.polym.net.ShopOfferData;
import com.junnio.polym.screen.SellerScreenHandler;
import com.junnio.polym.screen.ShopScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SellerScreen extends AbstractContainerScreen<SellerScreenHandler> {
    private static final Identifier BG = Identifier.withDefaultNamespace("textures/gui/container/villager.png");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller");
    private final List<ShopOfferData> offersView = new ArrayList<>();
    private int selected = 0;
    private int scrollOff = 0;
    private final OfferButton[] offerButtons = new OfferButton[7];
    public SellerScreen(SellerScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, title);
        this.imageWidth = 276;
        this.imageHeight = 166;
    }
    @Override
    protected void init() {
        super.init();

        int left = this.leftPos;
        int top  = this.topPos;

        int y = top + 16 + 2;
        for (int i = 0; i < 7; i++) {
            int row = i;
            offerButtons[i] = this.addRenderableWidget(new OfferButton(
                    left + 5, y, row, btn -> {
                        this.selected = row + this.scrollOff;
                    }
            ));
            y += 20;
        }
        int xBtn = left + 190;
        int yBtn = top + 18;
        this.addRenderableWidget(Button.builder(Component.literal("Add"), b ->
                ClientPlayNetworking.send(new AddOfferFromSlotsPayload())
        ).pos(xBtn, yBtn).size(60, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Save"), b ->
                ClientPlayNetworking.send(new SaveSellerOffersPayload(List.copyOf(this.offersView)))
        ).pos(xBtn, yBtn + 24).size(60, 20).build());

        offersView.clear();
        offersView.addAll(this.menu.getOffers());
        updateOfferButtons();
    }
    public void setOffersFromServer(List<ShopOfferData> offers) {
        offersView.clear();
        offersView.addAll(offers);
        updateOfferButtons();
    }
    private void updateOfferButtons() {
        for (int i = 0; i < 7; i++) {
            if (offerButtons[i] == null) continue;
            int idx = i + this.scrollOff;
            boolean has = idx >= 0 && idx < this.offersView.size();
            offerButtons[i].visible = has;
            offerButtons[i].active = has;
        }
        if (this.offersView.isEmpty()) this.selected = -1;
        else this.selected = Math.max(0, Math.min(this.selected, this.offersView.size() - 1));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        int left = this.leftPos;
        int top  = this.topPos;

        var offers = this.offersView;
        int visible = 7;
        for (int row = 0; row < visible; row++) {
            int idx = row + this.scrollOff;
            if (idx >= offers.size()) break;
            int yRow = top + 16 + row * 20;
            if (idx == this.selected) {
                g.fill(left + 5, yRow - 1, left + 5 + 88, yRow - 1 + 20, 0x66FFFFFF);
            }
            var o = offers.get(idx);

            int y = top + 16 + row * 20;
            int xBuyA = left + 6 + 5;
            int xBuyB = left + 6 + 5 + 18;
            int xSell = left + 6 + 5 + 18 + 18 + 24;

            g.renderFakeItem(o.buyA(), xBuyA, y);
            g.renderItemDecorations(this.font, o.buyA(), xBuyA, y);

            if (!o.buyB().isEmpty()) {
                g.renderFakeItem(o.buyB(), xBuyB, y);
                g.renderItemDecorations(this.font, o.buyB(), xBuyB, y);
            }

            g.renderFakeItem(o.sell(), xSell, y);
            g.renderItemDecorations(this.font, o.sell(), xSell, y);
        }

        this.renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float f, int i, int j) {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        g.blit(RenderPipelines.GUI_TEXTURED, BG, k, l, 0, 0, this.imageWidth, this.imageHeight, 512, 256);
    }
    @Environment(EnvType.CLIENT)
    class OfferButton extends Button.Plain {
        final int rowIndex;

        OfferButton(int x, int y, int rowIndex, Button.OnPress onPress) {
            super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.rowIndex = rowIndex;
        }
    }
}
