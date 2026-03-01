package com.junnio.polymclient.screen;

import com.junnio.polym.net.shop.ShopOfferData;
import com.junnio.polym.net.seller.AddOfferFromSlotsPayload;
import com.junnio.polym.net.seller.DeleteOfferPayload;
import com.junnio.polym.net.seller.EditOfferPayload;
import com.junnio.polym.net.seller.SaveSellerOffersPayload;
import com.junnio.polym.screen.SellerScreenHandler;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SellerScreen extends AbstractContainerScreen<SellerScreenHandler> {
    private static final Identifier BG = Identifier.withDefaultNamespace("textures/gui/container/villager.png");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller_disabled");
    private static final Identifier TRADE_ARROW_SPRITE = Identifier.withDefaultNamespace("container/villager/trade_arrow");
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
    private boolean draggingScroller = false;
    private static final int SCROLL_X = 94;
    private static final int SCROLL_Y = 18;
    private static final int KNOB_W = 6;
    private static final int KNOB_H = 27;
    private static final int TRACK_H = 139;
    private static final int LIST_X = 5;
    private static final int ROW_W = 88;
    private static final int ITEM_Y_OFF = 2;

    private static final int ITEM_X0 = 8;
    private static final int BUY_B_DX = 13;
    private static final int BUY_C_DX = 26;
    private static final int SELL_DX = 18 + 16 + 22;
    private static final int SELL_DX_B = 18 + 16 + 35;
    private static final float ITEM_SCALE = 0.5f;
    private static final float ITEM_OFF = (16f - 16f * ITEM_SCALE) / 2f;
    @Nullable private ItemStack previewTooltipStack = null;
    private ItemStack cachedTooltipKey = ItemStack.EMPTY;
    private List<ClientTooltipComponent> cachedTooltip = List.of();
    private long missingFlashUntilMs = 0L;
    private int missingFlashMask = 0;

    private static final int MISS_BUY_A = 1 << 0;
    private static final int MISS_SELL  = 1 << 1;
    public SellerScreen(SellerScreenHandler handler, Inventory inv, Component title) {
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
                    invalidateTooltipCache();
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
            var missing = getMissingRequiredSlots();
            if (!missing.isEmpty()) {
                this.minecraft.getToastManager().addToast(
                        net.minecraft.client.gui.components.toasts.SystemToast.multiline(
                                this.minecraft,
                                net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId.NARRATOR_TOGGLE,
                                Component.translatable("toast.message.title"),
                                Component.translatable("toast.message.content")
                        )
                );
                flashMissingSlots(missing);
                return;
            }
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
    private void invalidateTooltipCache() {
        this.cachedTooltipKey = ItemStack.EMPTY;
        this.cachedTooltip = List.of();
    }
    private void flashMissingSlots(List<Integer> missing) {
        int mask = 0;
        for (int id : missing) {
            if (id == SellerScreenHandler.SLOT_BUY_A) mask |= MISS_BUY_A;
            if (id == SellerScreenHandler.SLOT_SELL)  mask |= MISS_SELL;
        }
        this.missingFlashMask = mask;
        this.missingFlashUntilMs = System.currentTimeMillis() + 900; // 0.9s
    }
    private List<Integer> getMissingRequiredSlots() {
        List<Integer> missing = new ArrayList<>(2);

        if (this.menu.getSlot(SellerScreenHandler.SLOT_BUY_A).getItem().isEmpty()) {
            missing.add(SellerScreenHandler.SLOT_BUY_A);
        }
        if (this.menu.getSlot(SellerScreenHandler.SLOT_SELL).getItem().isEmpty()) {
            missing.add(SellerScreenHandler.SLOT_SELL);
        }
        return missing;
    }

    private boolean hasSelection() {
        return this.selected >= 0 && this.selected < this.offersView.size();
    }
    private void updateActionButtons() {
        boolean selection = hasSelection();
        boolean locked = (this.mode != ActionMode.NORMAL);

        this.addBtn.setMessage(Component.literal(this.mode == ActionMode.ADDING ? "Adding" : "Add"));
        this.editBtn.setMessage(Component.literal(this.mode == ActionMode.EDITING ? "Editing" : "Edit"));
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
        updateOfferButtons();
    }


    public void setOffersFromServer(List<ShopOfferData> offers) {
        offersView.clear();
        offersView.addAll(offers);
        updateOfferButtons();
        updateActionButtons();
        invalidateTooltipCache();
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
            int rowX = left + LIST_X;
            int yRow = top + LIST_Y0 + row * ROW_H;
            int yItem = yRow + ITEM_Y_OFF;

            int xBuyA = left + ITEM_X0;
            int xBuyB = xBuyA + BUY_B_DX;
            int xBuyC = xBuyA + BUY_C_DX;
            int xSell = xBuyA + SELL_DX;
            int xSellB = xBuyA + SELL_DX_B;
            if (idx == this.selected) {
                g.fill(rowX, yRow - 1, rowX + ROW_W, yRow - 1 + ROW_H, 0x66FFFFFF);
            }
            var o = offers.get(idx);

            renderScaledFakeItem(g, o.buyA(), xBuyA, yItem, ITEM_SCALE, ITEM_OFF);
            if (!o.buyB().isEmpty()) {
                renderScaledFakeItem(g, o.buyB(), xBuyB, yItem, ITEM_SCALE, ITEM_OFF);
            }
            if (!o.buyC().isEmpty()) {
                renderScaledFakeItem(g, o.buyC(), xBuyC, yItem, ITEM_SCALE, ITEM_OFF);
            }
            renderScaledFakeItem(g, o.sell(), xSell, yItem, ITEM_SCALE, ITEM_OFF);
            if (!o.sellB().isEmpty()) {
                renderScaledFakeItem(g, o.sellB(), xSellB, yItem, ITEM_SCALE, ITEM_OFF);
            }
            this.renderButtonArrows(g, this.leftPos, yRow +2);
        }
        this.renderScroller(g, mouseX, mouseY);
        if (this.previewOffer != null) {
            this.previewTooltipStack = null;
            ItemStack realA = this.menu.getSlot(SellerScreenHandler.SLOT_BUY_A).getItem();
            ItemStack realB = this.menu.getSlot(SellerScreenHandler.SLOT_BUY_B).getItem();
            ItemStack realC = this.menu.getSlot(SellerScreenHandler.SLOT_BUY_C).getItem();
            ItemStack realS = this.menu.getSlot(SellerScreenHandler.SLOT_SELL).getItem();
            ItemStack realSB = this.menu.getSlot(SellerScreenHandler.SLOT_SELL_B).getItem();

            boolean emptyA = realA.isEmpty();
            boolean emptyB = realB.isEmpty();
            boolean emptyC = realC.isEmpty();
            boolean emptyS = realS.isEmpty();
            boolean emptySB = realSB.isEmpty();
            int px = this.leftPos + 110;
            int py = this.topPos + 37;

            int xA  = px;
            int xB  = px + 26;
            int xC  = px + 52;
            int xS  = px + 110;
            int xSB = px + 136;
            ItemStack a = previewOffer.buyA();
            ItemStack b = previewOffer.buyB();
            ItemStack c = previewOffer.buyC();
            ItemStack s = previewOffer.sell();
            ItemStack sb = previewOffer.sellB();
            if (emptyA) {
                if (mouseX >= xA && mouseX < xA + 16 && mouseY >= py && mouseY < py + 16) this.previewTooltipStack = a;
                g.renderFakeItem(a, xA, py);
                g.renderItemDecorations(this.font, a, xA, py);
            }
            if (emptyB && !b.isEmpty()) {
                if (mouseX >= xB && mouseX < xB + 16 && mouseY >= py && mouseY < py + 16) this.previewTooltipStack = b;
                g.renderFakeItem(b, xB, py);
                g.renderItemDecorations(this.font, b, xB, py);
            }
            if (emptyC && !c.isEmpty()) {
                if (mouseX >= xC && mouseX < xC + 16 && mouseY >= py && mouseY < py + 16) this.previewTooltipStack = c;
                g.renderFakeItem(c, xC, py);
                g.renderItemDecorations(this.font, c, xC, py);
            }
            if (emptyS) {
                if (mouseX >= xS && mouseX < xS + 16 && mouseY >= py && mouseY < py + 16) this.previewTooltipStack = s;
                g.renderFakeItem(s, xS, py);
                g.renderItemDecorations(this.font, s, xS, py);
            }
            if (emptySB && !sb.isEmpty()) {
                if (mouseX >= xSB && mouseX < xSB + 16 && mouseY >= py && mouseY < py + 16) this.previewTooltipStack = sb;
                g.renderFakeItem(sb, xSB, py);
                g.renderItemDecorations(this.font, sb, xSB, py);
            }
        }
        if (this.previewTooltipStack != null && !this.previewTooltipStack.isEmpty()) {
            renderItemTooltipTextOnly(g, this.previewTooltipStack, mouseX, mouseY);
        }
        renderMissingSlotOverlay(g);
        this.renderTooltip(g, mouseX, mouseY);
    }
    private void renderMissingSlotOverlay(GuiGraphics g) {
        long now = System.currentTimeMillis();
        if (now >= this.missingFlashUntilMs || this.missingFlashMask == 0) return;

        boolean on = ((now / 120) % 2) == 0;
        int fill = on ? 0x66FF0000 : 0x22FF0000;

        if ((this.missingFlashMask & MISS_BUY_A) != 0) {
            drawSlotOverlay(g, SellerScreenHandler.SLOT_BUY_A, fill);
        }
        if ((this.missingFlashMask & MISS_SELL) != 0) {
            drawSlotOverlay(g, SellerScreenHandler.SLOT_SELL, fill);
        }
    }
    private void drawSlotOverlay(GuiGraphics g, int slotId, int color) {
        Slot slot = this.menu.getSlot(slotId);
        int x = this.leftPos + slot.x;
        int y = this.topPos + slot.y;
        g.fill(x, y, x + 16, y + 16, color);
    }
    private void renderItemTooltipTextOnly(GuiGraphics g, ItemStack stack, int mouseX, int mouseY) {
        if (stack == null || stack.isEmpty()) return;

        ItemStack key = stack.copy();
        key.setCount(1);

        if (cachedTooltipKey.isEmpty() || !ItemStack.isSameItemSameComponents(key, cachedTooltipKey)) {
            cachedTooltipKey = key;
            cachedTooltip = buildTooltipComponents(stack);
        }

        g.renderTooltip(this.font, cachedTooltip, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
    }
    private List<ClientTooltipComponent> buildTooltipComponents(ItemStack stack) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return List.of();

        var ctx = net.minecraft.world.item.Item.TooltipContext.of(player.level());
        var flag = mc.options.advancedItemTooltips
                ? net.minecraft.world.item.TooltipFlag.ADVANCED
                : net.minecraft.world.item.TooltipFlag.NORMAL;

        List<Component> lines = stack.getTooltipLines(ctx, player, flag);

        int maxWidth = 240;
        List<ClientTooltipComponent> comps = new ArrayList<>(lines.size());

        for (Component c : lines) {
            List<FormattedCharSequence> baked = this.font.split(c, maxWidth);
            for (FormattedCharSequence seq : baked) {
                comps.add(new ClientTextTooltip(seq));
            }
        }
        return comps;
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
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TRADE_ARROW_SPRITE, baseX + 5 + 30 + 20, rowY + 3, 10, 9);
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
        updateActionButtons();
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
        if (this.draggingScroller) return true;
        if (super.mouseScrolled(mouseX, mouseY, dx, dy)) return true;
        if (!canScroll()) return false;

        this.scrollOff = Mth.clamp((int)(this.scrollOff - dy), 0, maxScroll());
        updateOfferButtons();
        updateActionButtons();
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        if (this.draggingScroller && canScroll()) {
            int i = this.offersView.size();
            int j = this.topPos + SCROLL_Y;
            int k = j + TRACK_H;
            int l = i - 7;

            float f = ((float)mouseButtonEvent.y() - (float)j - (KNOB_H / 2.0f)) / ((float)(k - j) - (float)KNOB_H);
            f = f * (float)l + 0.5f;
            this.scrollOff = Mth.clamp((int)f, 0, l);

            updateOfferButtons();
            updateActionButtons();
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
            invalidateTooltipCache();
            this.rowIndex = rowIndex;
        }
    }
}