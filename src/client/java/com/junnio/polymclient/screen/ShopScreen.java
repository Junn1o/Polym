package com.junnio.polymclient.screen;

import com.junnio.polym.net.*;
import com.junnio.polym.screen.ShopScreenHandler;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ShopScreen extends AbstractContainerScreen<ShopScreenHandler> {
    private static final Identifier BG = Identifier.withDefaultNamespace("textures/gui/container/villager.png");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller_disabled");
    private static final Identifier TRADE_ARROW_SPRITE = Identifier.withDefaultNamespace("container/villager/trade_arrow");
    private int selected = -1;
    private int scrollOff = 0;
    private final OfferButton[] offerButtons = new OfferButton[7];
    private static final int LIST_Y0 = 18;
    private static final int ROW_H = 20;
    private boolean draggingScroller = false;
    private static final int SCROLL_X = 94;
    private static final int SCROLL_Y = 18;
    private static final int KNOB_W = 6;
    private static final int KNOB_H = 27;
    private static final int TRACK_H = 139;
    private static final int LIST_X = 5;
    private static final int ROW_W = 88;
    private static final int ITEM_Y_OFF = 2;

    private static final int ITEM_X0 = 11; // 6+5
    private static final int BUY_B_DX = 18;
    private static final int SELL_DX = 18 + 18 + 24;
    private static final float ITEM_SCALE = 0.75f;
    private static final float ITEM_OFF = (16f - 16f * ITEM_SCALE) / 2f;
    private final List<ShopOfferViewData> offersView = new ArrayList<>();
    @Nullable
    private ShopOfferViewData previewOffer = null;
    public ShopScreen(ShopScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, title);
        this.imageWidth = 276;
        this.imageHeight = 166;
        this.inventoryLabelX = 107;
    }
    @Override
    protected void init() {
        super.init();

        int left = this.leftPos;
        int top  = this.topPos;

        int y = top + LIST_Y0;
        for (int i = 0; i < 7; i++) {
            int row = i;
            offerButtons[i] = this.addRenderableWidget(new OfferButton(
                    left + 5, y, row, btn -> {
                int idx = row + this.scrollOff;
                this.selected = idx;

                if (idx >= 0 && idx < this.offersView.size()) {
                    this.previewOffer = this.offersView.get(idx);
                } else {
                    this.previewOffer = null;
                }
            }
            ));
            y += ROW_H;
        }
        int btnW = 60;
        int btnH = 25;

        int xOutside = this.leftPos + this.imageWidth;
        int yBase = this.topPos;
        offersView.clear();
        offersView.addAll(this.menu.getOffers());
        updateOfferButtons();
    }

    @Nullable
    private PlayerInfo getPlayerInfo(UUID uuid) {
        var conn = Minecraft.getInstance().getConnection();
        if (conn == null) return null;
        return conn.getPlayerInfo(uuid);
    }

    private void renderOwnerHead(GuiGraphics g, int x, int y, UUID ownerUuid) {
        PlayerInfo info = getPlayerInfo(ownerUuid);
        if (info == null) return;

        Identifier skin = info.getSkin().body().texturePath();

        g.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 8.0f, 8.0f, 8, 8, 64, 64);
        g.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 40.0f, 8.0f, 8, 8, 64, 64);
    }
    public void setOffersFromServer(List<ShopOfferViewData> offers) {
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
            int rowX = left + LIST_X;
            int yRow = top + LIST_Y0 + row * ROW_H;
            int yItem = yRow + ITEM_Y_OFF;

            int xBuyA = left + ITEM_X0;
            int xBuyB = xBuyA + BUY_B_DX;
            int xSell = xBuyA + SELL_DX;
            if (idx == this.selected) {
                g.fill(rowX, yRow - 1, rowX + ROW_W, yRow - 1 + ROW_H, 0x66FFFFFF);
            }
            var view = offers.get(idx);
            var o = view.offer();

            renderScaledFakeItem(g, o.buyA(), xBuyA, yItem, ITEM_SCALE, ITEM_OFF);
            if (!o.buyB().isEmpty()) {
                renderScaledFakeItem(g, o.buyB(), xBuyB, yItem, ITEM_SCALE, ITEM_OFF);
            }
            renderScaledFakeItem(g, o.sell(), xSell, yItem, ITEM_SCALE, ITEM_OFF);
            this.renderButtonArrows(g, this.leftPos, yRow +2);
        }
        this.renderScroller(g, mouseX, mouseY);
        if (this.previewOffer != null) {
            ItemStack realA = this.menu.getSlot(ShopScreenHandler.SLOT_BUY_A).getItem();
            ItemStack realB = this.menu.getSlot(ShopScreenHandler.SLOT_BUY_B).getItem();
            ItemStack realS = this.menu.getSlot(ShopScreenHandler.SLOT_SELL).getItem();

            boolean emptyA = realA.isEmpty();
            boolean emptyB = realB.isEmpty();
            boolean emptyS = realS.isEmpty();
            int px = this.leftPos + 136;
            int py = this.topPos + 37;

            ItemStack a = previewOffer.offer().buyA();
            ItemStack b = previewOffer.offer().buyB();
            ItemStack s = previewOffer.offer().sell();

            if (emptyA) {
                g.renderFakeItem(a, px, py);
                g.renderItemDecorations(this.font, a, px, py);
            }
            if (emptyB && !b.isEmpty()) {
                g.renderFakeItem(b, px + 26, py);
                g.renderItemDecorations(this.font, b, px + 26, py);
            }
            if (emptyS) {
                g.renderFakeItem(s, px + 26 + 58, py);
                g.renderItemDecorations(this.font, s, px + 26 + 58, py);
            }
            int hx = this.leftPos + this.imageWidth - 16 - 6;
            int hy = this.topPos + 6;
            renderOwnerHead(g, hx, hy, previewOffer.ownerUuid());
        }
        this.renderTooltip(g, mouseX, mouseY);
    }
    private void renderScaledFakeItem(GuiGraphics g, ItemStack stack, int x, int y, float scale, float itemoff) {
        if (stack.isEmpty()) return;

        var m = g.pose();
        m.pushMatrix();
        m.translate(x + itemoff, y + itemoff);
        m.scale(scale, scale);

        g.renderFakeItem(stack, 0, 0);
        g.renderItemDecorations(this.font, stack, 0, 0);

        m.popMatrix();
    }
    private void renderButtonArrows(GuiGraphics guiGraphics, int baseX, int rowY) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TRADE_ARROW_SPRITE, baseX + 5 + 35 + 20, rowY + 3, 10, 9);

    }
    private void renderScroller(GuiGraphics g, int mouseX, int mouseY) {
        int sx = this.leftPos + SCROLL_X;
        int syBase = this.topPos + SCROLL_Y;
        if (canScroll()){
            int sy = syBase + scrollerY();
            g.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE, sx, sy, KNOB_W, KNOB_H);
            if (mouseX >= sx && mouseX < sx + KNOB_W && mouseY >= sy && mouseY <= sy + KNOB_H) {
                g.requestCursor(this.draggingScroller ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
            }
        } else{
            g.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_DISABLED_SPRITE, sx, syBase, KNOB_W, KNOB_H);
        }
    }
    private boolean canScroll() {
        return this.offersView.size() > 7;
    }

    private int maxScroll() {
        return Math.max(0, this.offersView.size() - 7);
    }
    private boolean isMouseOverScrollTrack(double mx, double my) {
        int x0 = this.leftPos + SCROLL_X;
        int y0 = this.topPos + SCROLL_Y;
        return canScroll()
                && mx > x0 && mx < (x0 + KNOB_W)
                && my > y0 && my <= (y0 + TRACK_H + 1);
    }
    private void setScrollOffFromMouseY(double mouseY) {
        int max = maxScroll();
        int n = TRACK_H - KNOB_H;
        double myRelative = mouseY - (this.topPos + SCROLL_Y) - (KNOB_H / 2.0);
        double t = Mth.clamp(myRelative / (double)n, 0.0, 1.0);
        this.scrollOff = (int)Math.round(t * max);
        updateOfferButtons();
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
        if (this.draggingScroller) return true;
        if (super.mouseScrolled(mouseX, mouseY, dx, dy)) return true;
        if (!canScroll()) return false;

        this.scrollOff = Mth.clamp((int)(this.scrollOff - dy), 0, maxScroll());
        updateOfferButtons();
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        if (this.draggingScroller && canScroll()) {
            int i = this.offersView.size();
            int j = this.topPos + SCROLL_Y;      // top + 18
            int k = j + TRACK_H;                 // +139
            int l = i - 7;                       // maxScroll

            float f = ((float)mouseButtonEvent.y() - (float)j - (KNOB_H / 2.0f)) / ((float)(k - j) - (float)KNOB_H);
            f = f * (float)l + 0.5f;
            this.scrollOff = Mth.clamp((int)f, 0, l);

            updateOfferButtons();
            return true;
        }
        return super.mouseDragged(mouseButtonEvent, d, e);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.button()==0 && this.draggingScroller) {
            this.draggingScroller = false;
            return true;
        }
        return super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button()==0 && canScroll() && isMouseOverScrollTrack(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.draggingScroller = true;
            setScrollOffFromMouseY(mouseButtonEvent.y());
            return true;
        }
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    private int scrollerY() {
        int total = offersView.size();
        int max = total - 7;
        if (max <= 0) return 0;

        int track = 139;
        int knobH = 27;

        int n = track - knobH;
        float t = (float)scrollOff / (float)max;
        return (int)(t * n);
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
