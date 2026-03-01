package com.junnio.polymclient.screen;

import com.junnio.polym.net.shop.ShopOfferViewData;
import com.junnio.polym.screen.SellerScreenHandler;
import com.junnio.polym.screen.ShopScreenHandler;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ShopScreen extends AbstractContainerScreen<ShopScreenHandler> {
    private static final Identifier BG = Identifier.withDefaultNamespace("textures/gui/container/villager.png");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller_disabled");
    private static final Identifier TRADE_ARROW_SPRITE = Identifier.withDefaultNamespace("container/villager/trade_arrow");
    private static final Identifier SEARCH_SPRITE = Identifier.withDefaultNamespace("icon/search");
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

    private static final int ITEM_X0 = 8;
    private static final int BUY_B_DX = 13;
    private static final int BUY_C_DX = 26;
    private static final int SELL_DX = 18 + 16 + 22;
    private static final int SELL_DX_B = 18 + 16 + 35;
    private static final float ITEM_SCALE = 0.5f;
    private static final float ITEM_OFF = (16f - 16f * ITEM_SCALE) / 2f;
    private final List<ShopOfferViewData> offersView = new ArrayList<>();
    private EditBox searchBox;
    private String lastQuery = "";
    private final List<ShopOfferViewData> allOffers = new ArrayList<>();
    @Nullable
    private ShopOfferViewData previewOffer = null;
    @Nullable private ItemStack previewTooltipStack = null;
    private ItemStack cachedTooltipKey = ItemStack.EMPTY;
    private List<ClientTooltipComponent> cachedTooltip = List.of();
    @Nullable private UUID cachedOwnerUuid = null;
    @Nullable private OwnerTooltip cachedOwnerTooltip = null;
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
        allOffers.clear();
        allOffers.addAll(this.menu.getOffers());
        applyFilter("");
        updateOfferButtons();

        this.searchBox = new EditBox(this.font, this.leftPos + 119, this.topPos + 4, 80, 12, Component.literal("Search"));
        this.searchBox.setMaxLength(64);
        this.searchBox.setBordered(true);
        this.searchBox.setVisible(true);
        this.searchBox.setValue("");
        this.addRenderableWidget(this.searchBox);
        if (this.searchBox != null) this.searchBox.setFocused(false);
        this.setFocused(null);
    }
    @Nullable
    private ShopOfferViewData getHoveredOffer(int mouseX, int mouseY) {
        int x0 = this.leftPos + LIST_X;
        int y0 = this.topPos + LIST_Y0;
        int x1 = x0 + ROW_W;
        int y1 = y0 + 7 * ROW_H;

        if (mouseX < x0 || mouseX >= x1 || mouseY < y0 || mouseY >= y1) return null;

        int row = (mouseY - y0) / ROW_H;
        int idx = row + this.scrollOff;

        if (row < 0 || row >= 7) return null;
        if (idx < 0 || idx >= this.offersView.size()) return null;

        return this.offersView.get(idx);
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
        var pose = g.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(1.0f, 1.0f);
        g.blit(RenderPipelines.GUI_TEXTURED, skin, 0, 0, 8.0f, 8.0f, 8, 8, 64, 64);
        g.blit(RenderPipelines.GUI_TEXTURED, skin, 0, 0, 40.0f, 8.0f, 8, 8, 64, 64);

        pose.popMatrix();
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
            int xBuyC = xBuyA + BUY_C_DX;
            int xSell = xBuyA + SELL_DX;
            int xSellB = xBuyA + SELL_DX_B;
            if (idx == this.selected) {
                g.fill(rowX, yRow - 1, rowX + ROW_W, yRow - 1 + ROW_H, 0x66FFFFFF);
            }
            var view = offers.get(idx);
            var o = view.offer();

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
            ItemStack a = previewOffer.offer().buyA();
            ItemStack b = previewOffer.offer().buyB();
            ItemStack c = previewOffer.offer().buyC();
            ItemStack s = previewOffer.offer().sell();
            ItemStack sb = previewOffer.offer().sellB();

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
            int hx = this.leftPos + this.imageWidth - 16 - 6;
            int hy = this.topPos + 6;

            renderOwnerHead(g, hx, hy, previewOffer.ownerUuid());

            String name = previewOffer.ownerName();
            if (name != null && !name.isBlank()) {
                int gap = 4;
                int maxW = 80;
                name = this.font.plainSubstrByWidth(name, maxW);

                float scale = 0.75f;
                int wScaled = (int)(this.font.width(name) * scale);
                int tx = hx - gap - wScaled;

                int ty = hy + (16 - (int)(this.font.lineHeight * scale)) / 2;
                ty -= 3;
                var pose = g.pose();
                pose.pushMatrix();
                pose.translate(tx, ty);
                pose.scale(scale, scale);
                g.drawString(this.font, name, 0, 0, 0xFFFFFFFF, true);
                pose.popMatrix();
            }
        }
        ShopOfferViewData hovered = getHoveredOffer(mouseX, mouseY);
        if (hovered != null) {
            UUID u = hovered.ownerUuid();
            if (cachedOwnerTooltip == null || cachedOwnerUuid == null || !cachedOwnerUuid.equals(u)) {
                cachedOwnerUuid = u;
                cachedOwnerTooltip = new OwnerTooltip(u, hovered.ownerName());
            }
            g.renderTooltip(this.font, List.of(cachedOwnerTooltip), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
        String q = this.searchBox != null ? this.searchBox.getValue() : "";
        if (!q.equals(this.lastQuery)) {
            this.lastQuery = q;
            applyFilter(q);
        }
        renderSearchIcon(g, this.leftPos + 105, this.topPos + 4);
        if (this.previewTooltipStack != null && !this.previewTooltipStack.isEmpty()) {
            renderItemTooltipTextOnly(g, this.previewTooltipStack, mouseX, mouseY);
        }
        this.renderTooltip(g, mouseX, mouseY);
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
    private void renderSearchIcon(GuiGraphics guiGraphics, int baseX, int rowY) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SEARCH_SPRITE, baseX, rowY, 12, 12);
    }
    private static String fold(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");
        n = n.replace('đ', 'd').replace('Đ', 'D');
        return n.toLowerCase();
    }
    private void applyFilter(String queryRaw) {
        String q = fold(queryRaw).trim();
        if (q.isEmpty()) {
            offersView.clear();
            offersView.addAll(allOffers);
            this.scrollOff = Mth.clamp(this.scrollOff, 0, Math.max(0, offersView.size() - 7));
            updateOfferButtons();
            return;
        }

        offersView.clear();

        for (ShopOfferViewData view : allOffers) {
            var offer = view.offer();

            boolean ok = matchesStack(offer.sell(), q) || matchesStack(offer.sellB(), q);
            if (ok) offersView.add(view);
        }
        this.scrollOff = 0;
        this.selected = offersView.isEmpty() ? -1 : 0;
        this.previewOffer = offersView.isEmpty() ? null : offersView.get(0);

        updateOfferButtons();
    }
    private boolean matchesStack(ItemStack stack, String q) {
        if (stack == null || stack.isEmpty()) return false;

        String id = "";
        var key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (key != null) id = key.toString();

        String name = stack.getHoverName().getString();
        return fold(id).contains(q) || fold(name).contains(q);
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
            int j = this.topPos + SCROLL_Y;
            int k = j + TRACK_H;
            int l = i - 7;

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
    public boolean keyPressed(KeyEvent keyEvent) {
        if (this.searchBox != null && this.searchBox.isFocused()) {
            if (this.searchBox.keyPressed(keyEvent)) {
                return true;
            }
            var invKey = Minecraft.getInstance().options.keyInventory;
            if (invKey.matches(keyEvent)) {
                return true;
            }
            var keyCode = org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
            if (keyEvent.key() == keyCode) {
                this.searchBox.setFocused(false);
                this.setFocused(null);
                return true;
            }
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button()==0 && canScroll() && isMouseOverScrollTrack(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.draggingScroller = true;
            setScrollOffFromMouseY(mouseButtonEvent.y());
            return true;
        }
        if (this.searchBox != null) {
            boolean inside = this.searchBox.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y());
            if (!inside) {
                this.searchBox.setFocused(false);
                this.setFocused(null);
            }
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
            cachedTooltipKey = ItemStack.EMPTY;
            cachedTooltip = List.of();
            this.rowIndex = rowIndex;
        }
    }
    private class OwnerTooltip implements ClientTooltipComponent {
        private static final int ICON = 8;
        private static final int GAP = 4;
        private static final int MAX_W = 120;

        private final UUID uuid;
        private final String name;
        private final String fitted;

        OwnerTooltip(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name == null ? "" : name;
            int textMax = MAX_W - ICON - GAP;
            this.fitted = ShopScreen.this.font.plainSubstrByWidth(this.name, Math.max(0, textMax));
        }

        @Override
        public int getWidth(Font font) {
            int wText = font.width(fitted);
            return Math.min(MAX_W, ICON + GAP + wText);
        }

        @Override
        public int getHeight(Font font) {
            return ICON;
        }

        @Override
        public void renderText(GuiGraphics g, Font font, int x, int y) {
            int ty = y + (ICON - font.lineHeight) / 2;
            g.drawString(font, fitted, x + ICON + GAP, ty, 0xFFFFFFFF, true);
        }

        @Override
        public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics g) {
            PlayerInfo info = ShopScreen.this.getPlayerInfo(uuid);

            if (info == null) {
                g.fill(x, y, x + ICON, y + ICON, 0xFF3A3A3A);
                g.drawString(font, "?", x + 5, y + 4, 0xFFFFFFFF, true);
                return;
            }

            Identifier skin = info.getSkin().body().texturePath();

            var pose = g.pose();
            pose.pushMatrix();
            pose.translate(x, y);
            pose.scale(1.0f, 1.0f);

            g.blit(RenderPipelines.GUI_TEXTURED, skin, 0, 0, 8.0f, 8.0f, 8, 8, 64, 64);
            g.blit(RenderPipelines.GUI_TEXTURED, skin, 0, 0, 40.0f, 8.0f, 8, 8, 64, 64);

            pose.popMatrix();
        }
    }
}
