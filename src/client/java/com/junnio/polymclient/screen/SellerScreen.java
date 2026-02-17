package com.junnio.polymclient.screen;

import com.junnio.polym.Polym;
import com.junnio.polym.screen.SellerScreenHandler;
import com.junnio.polym.screen.ShopScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class SellerScreen extends AbstractContainerScreen<SellerScreenHandler> {
    private static final Identifier BG = Identifier.withDefaultNamespace("textures/gui/container/villager.png");

    public SellerScreen(SellerScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, title);
        this.imageWidth = 276;
    }
    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        int left = this.leftPos;
        int top  = this.topPos;

        var offers = this.menu.getOffers();
        int visible = 7;

        for (int row = 0; row < visible; row++) {
            int idx = row;
            if (idx >= offers.size()) break;

            var o = offers.get(idx);

            int y = top + 16 + row * 20;
            int xBuyA = left + 6 + 5;
            int xBuyB = left + 6 + 5 + 18;
            int xSell = left + 6 + 5 + 18 + 18 + 24;

            g.renderItem(o.buyA(), xBuyA, y);
            g.renderItemDecorations(this.font, o.buyA(), xBuyA, y);

            if (!o.buyB().isEmpty()) {
                g.renderItem(o.buyB(), xBuyB, y);
                g.renderItemDecorations(this.font, o.buyB(), xBuyB, y);
            }

            g.renderItem(o.sell(), xSell, y);
            g.renderItemDecorations(this.font, o.sell(), xSell, y);
        }

        this.renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float f, int i, int j) {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        g.blit(RenderPipelines.GUI_TEXTURED, BG, k, l, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
    }
}
