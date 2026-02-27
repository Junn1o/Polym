package com.junnio.polym.screen;

import com.junnio.polym.net.shop.ShopOfferData;
import com.junnio.polym.net.seller.SellerOfferData;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SellerScreenHandler extends AbstractContainerMenu {
    private final List<ShopOfferData> offers;
    public List<ShopOfferData> getOffers() {
        return List.copyOf(this.offers);
    }
    private final Container offerInv = new SimpleContainer(5);
    public static final int SLOT_BUY_A = 0;
    public static final int SLOT_BUY_B = 1;
    public static final int SLOT_BUY_C = 2;
    public static final int SLOT_SELL  = 3;
    public static final int SLOT_SELL_B  = 4;
    public SellerScreenHandler(int syncId, Inventory playerInventory, Player player, SellerOfferData data) {
        super(ModScreenHandlers.SELLER_SCREEN_HANDLER, syncId);
        this.offers = new ArrayList<>(data.offers());
        this.addSlot(new Slot(offerInv, SLOT_BUY_A,  110, 37));
        this.addSlot(new Slot(offerInv, SLOT_BUY_B,  136, 37));
        this.addSlot(new Slot(offerInv, SLOT_BUY_C,  162, 37));
        this.addSlot(new Slot(offerInv, SLOT_SELL,   220, 37));
        this.addSlot(new Slot(offerInv, SLOT_SELL_B,   220, 60));
        this.addStandardInventorySlots(playerInventory, 108, 84);
    }
    public void editOfferFromSlotsAndClear(int index) {
        if (index < 0 || index >= offers.size()) return;

        ItemStack a = offerInv.getItem(0).copy();
        ItemStack b = offerInv.getItem(1).copy();
        ItemStack c = offerInv.getItem(2).copy();
        ItemStack s = offerInv.getItem(3).copy();
        ItemStack sb = offerInv.getItem(4).copy();
        if (a.isEmpty() || s.isEmpty()) return;

        offers.set(index, new ShopOfferData(a, b.isEmpty()?ItemStack.EMPTY:b, c.isEmpty()?ItemStack.EMPTY:c, s, sb.isEmpty()?ItemStack.EMPTY:sb));
        offerInv.setItem(SLOT_BUY_A, ItemStack.EMPTY);
        offerInv.setItem(SLOT_BUY_B, ItemStack.EMPTY);
        offerInv.setItem(SLOT_BUY_C, ItemStack.EMPTY);
        offerInv.setItem(SLOT_SELL,  ItemStack.EMPTY);
        offerInv.setItem(SLOT_SELL_B,  ItemStack.EMPTY);
        this.broadcastChanges();
    }

    public void deleteOffer(int index) {
        if (index < 0 || index >= offers.size()) return;
        offers.remove(index);
        broadcastChanges();
    }
    public void addOfferFromSlots() {
        ItemStack a = offerInv.getItem(0).copy();
        ItemStack b = offerInv.getItem(1).copy();
        ItemStack c = offerInv.getItem(2).copy();
        ItemStack s = offerInv.getItem(3).copy();
        ItemStack sb = offerInv.getItem(4).copy();
        if (a.isEmpty() || s.isEmpty()) return;

        offers.add(new ShopOfferData(a, b.isEmpty()?ItemStack.EMPTY:b, c.isEmpty()?ItemStack.EMPTY:c, s, sb.isEmpty()?ItemStack.EMPTY:sb));
        offerInv.setItem(SLOT_BUY_A, ItemStack.EMPTY);
        offerInv.setItem(SLOT_BUY_B, ItemStack.EMPTY);
        offerInv.setItem(SLOT_BUY_C, ItemStack.EMPTY);
        offerInv.setItem(SLOT_SELL,  ItemStack.EMPTY);
        offerInv.setItem(SLOT_SELL_B,  ItemStack.EMPTY);
        this.broadcastChanges();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        ItemStack carried = this.getCarried();
        if (!carried.isEmpty()) {
            if (!player.getInventory().add(carried)) {
                player.drop(carried, false);
            }
            this.setCarried(ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < 5) {
            if (clickType != ClickType.PICKUP && clickType != ClickType.PICKUP_ALL) {
                return;
            }

            ItemStack carried = this.getCarried();
            ItemStack current = this.offerInv.getItem(slotId);

            if (carried.isEmpty()) {
                this.offerInv.setItem(slotId, ItemStack.EMPTY);
                this.broadcastChanges();
                return;
            }
            ItemStack ghost = carried.copy();

            if (button == 1) {
                ghost.setCount(1);
                 if (!current.isEmpty() && ItemStack.isSameItemSameComponents(current, ghost)) {
                     ghost.setCount(Math.min(current.getCount() + 1, ghost.getMaxStackSize()));
                 }
            } else {
                ghost.setCount(Math.min(carried.getCount(), ghost.getMaxStackSize()));
            }

            this.offerInv.setItem(slotId, ghost);

            this.broadcastChanges();

            return;
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
