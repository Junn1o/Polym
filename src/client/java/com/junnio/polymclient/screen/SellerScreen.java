package com.junnio.polymclient.screen;

import com.junnio.polym.net.*;
import com.junnio.polym.screen.SellerScreenHandler;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SellerScreen extends AbstractContainerScreen<SellerScreenHandler> {
    private static final Identifier BG = Identifier.withDefaultNamespace("textures/gui/container/villager.png");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("textures/gui/sprites/container/villager/scroller.png");
    private final List<ShopOfferData> offersView = new ArrayList<>();
    private int selected = -1;
    private int scrollOff = 0;
    private final OfferButton[] offerButtons = new OfferButton[7];
    @Nullable
    private ShopOfferData previewOffer = null;
    private enum ActionMode { NORMAL, ADDING, EDITING }
    private ActionMode mode = ActionMode.NORMAL;
    private Button addBtn, saveBtn, deleteBtn, editBtn, cancelBtn;
    private static final int LIST_Y0 = 18;
    private static final int ROW_H = 20;
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

                updateActionButtons();
            }
            ));
            y += ROW_H;
        }
        int btnW = 60;
        int btnH = 25;

        int xOutside = this.leftPos + this.imageWidth;
        int yBase = this.topPos;

        this.addBtn = this.addRenderableWidget(Button.builder(Component.literal("Add"), b -> {
            this.mode = ActionMode.ADDING;
            updateActionButtons();
        }).pos(xOutside, yBase).size(btnW, btnH).build());

        this.editBtn = this.addRenderableWidget(Button.builder(Component.literal("Edit"), b -> {
            if (!hasSelection()) return;
            this.mode = ActionMode.EDITING;
            updateActionButtons();
        }).pos(xOutside, yBase + 24).size(btnW, btnH).build());

        this.saveBtn = this.addRenderableWidget(Button.builder(Component.literal("Save"), b -> {
            if (!validateRequiredSlots()) return;

            if (this.mode == ActionMode.ADDING) {
                ClientPlayNetworking.send(new AddOfferFromSlotsPayload());
            } else if (this.mode == ActionMode.EDITING) {
                if (!hasSelection()) return;
                ClientPlayNetworking.send(new EditOfferPayload(this.selected));
            }

            ClientPlayNetworking.send(new SaveSellerOffersPayload());

            this.mode = ActionMode.NORMAL;
            updateActionButtons();
        }).pos(xOutside, yBase + 72).size(btnW, btnH).build());

        this.deleteBtn = this.addRenderableWidget(Button.builder(Component.literal("Delete"), b -> {
            if (hasSelection()) {
                ClientPlayNetworking.send(new DeleteOfferPayload(this.selected));
                ClientPlayNetworking.send(new SaveSellerOffersPayload());
            }
        }).pos(xOutside, yBase + 48).size(btnW, btnH).build());
        this.cancelBtn = this.addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> {
            this.mode = ActionMode.NORMAL;
            this.previewOffer = null;
            updateActionButtons();
        }).pos(xOutside, yBase + 96).size(btnW, btnH).build());
        offersView.clear();
        offersView.addAll(this.menu.getOffers());
        updateOfferButtons();
        updateActionButtons();
    }
    private boolean validateRequiredSlots() {
        ItemStack a = this.menu.getSlot(SellerScreenHandler.SLOT_BUY_A).getItem();
        ItemStack s = this.menu.getSlot(SellerScreenHandler.SLOT_SELL).getItem();
        return !a.isEmpty() && !s.isEmpty();
    }

    private boolean hasSelection() {
        return this.selected >= 0 && this.selected < this.offersView.size();
    }
    private void updateActionButtons() {
        boolean selection = hasSelection();
        boolean locked = (this.mode != ActionMode.NORMAL);

        this.addBtn.setMessage(Component.literal(this.mode == ActionMode.ADDING ? "Adding" : "Add"));
        this.editBtn.setMessage(Component.literal(this.mode == ActionMode.EDITING ? "Editting" : "Edit"));
        this.cancelBtn.visible = locked;
        this.cancelBtn.active = locked;
        this.saveBtn.visible = true;
        if (locked)
            this.saveBtn.active = true;
        else
            this.saveBtn.active = false;
        this.addBtn.active    = !locked;
        this.editBtn.active   = !locked && selection;
        this.deleteBtn.active = !locked && selection;
    }


    public void setOffersFromServer(List<ShopOfferData> offers) {
        offersView.clear();
        offersView.addAll(offers);
        updateOfferButtons();
        updateActionButtons();
    }
    private void updateOfferButtons() {
        boolean locked = (this.mode != ActionMode.NORMAL);
        for (int i = 0; i < 7; i++) {
            if (offerButtons[i] == null) continue;
            int idx = i + this.scrollOff;
            boolean has = idx >= 0 && idx < this.offersView.size();
            offerButtons[i].visible = has;
            offerButtons[i].active = has && !locked;
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
            int yRow = top + LIST_Y0 + row * ROW_H;
            if (idx == this.selected) {
                g.fill(left + 5, yRow - 1, left + 5 + 88, yRow - 1 + 20, 0x66FFFFFF);
            }
            var o = offers.get(idx);

            int y = top + LIST_Y0 + 2 + row * ROW_H;
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
        int sx = this.leftPos + 94;
        int sy = this.topPos + 18 + scrollerY();
        g.blit(RenderPipelines.GUI_TEXTURED, BG, sx, sy, /*u*/ 0, /*v*/ 199, 6, 27, 512, 256);
        if (this.previewOffer != null) {
            ItemStack realA = this.menu.getSlot(SellerScreenHandler.SLOT_BUY_A).getItem();
            ItemStack realB = this.menu.getSlot(SellerScreenHandler.SLOT_BUY_B).getItem();
            ItemStack realS = this.menu.getSlot(SellerScreenHandler.SLOT_SELL).getItem();

            boolean emptyA = realA.isEmpty();
            boolean emptyB = realB.isEmpty();
            boolean emptyS = realS.isEmpty();
            int px = this.leftPos + 136;
            int py = this.topPos + 37;

            ItemStack a = previewOffer.buyA();
            ItemStack b = previewOffer.buyB();
            ItemStack s = previewOffer.sell();

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
        }
        this.renderTooltip(g, mouseX, mouseY);
    }

    private boolean canScroll() {
        return this.offersView.size() > 7;
    }

    private int maxScroll() {
        return Math.max(0, this.offersView.size() - 7);
    }
    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        if (super.mouseScrolled(d, e, f, g)) return true;
        if (!canScroll()) return false;
        this.scrollOff = Mth.clamp((int)(this.scrollOff - g), 0, maxScroll());
        updateOfferButtons();
        return true;
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