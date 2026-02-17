package com.junnio.polym.screen;

import com.junnio.polym.net.ShopOfferData;
import com.junnio.polym.net.ShopOpenData;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SellerScreenHandler extends AbstractContainerMenu {
    private final Level world;
    private final Player playerentity;
    private final List<ShopOfferData> offers;
    public List<ShopOfferData> getOffers() {
        return List.copyOf(this.offers);
    }
    private final Container offerInv = new SimpleContainer(3);
    public static final int SLOT_BUY_A = 0;
    public static final int SLOT_BUY_B = 1;
    public static final int SLOT_SELL  = 2;
    public SellerScreenHandler(int syncId, Inventory playerInventory, Player player, ShopOpenData data) {
        super(ModScreenHandlers.SELLER_SCREEN_HANDLER, syncId);
        this.world = playerInventory.player.level();
        this.playerentity = player;
        this.offers = new ArrayList<>(data.offers());
        this.addStandardInventorySlots(playerInventory, 108, 84);
        this.addSlot(new Slot(offerInv, SLOT_BUY_A,  /*x*/ 136, /*y*/ 37));
        this.addSlot(new Slot(offerInv, SLOT_BUY_B,  /*x*/ 154, /*y*/ 37));
        this.addSlot(new Slot(offerInv, SLOT_SELL,   /*x*/ 208, /*y*/ 37));
    }
    public void addOfferFromSlotsAndClear() {
        ItemStack a = offerInv.getItem(0).copy();
        ItemStack b = offerInv.getItem(1).copy();
        ItemStack s = offerInv.getItem(2).copy();

        if (a.isEmpty() || s.isEmpty()) return; // buyA & sell bắt buộc

        a.setCount(1);              // hoặc giữ count user đặt trong slot
        if (!b.isEmpty()) b.setCount(1);
        s.setCount(1);

        offers.add(new ShopOfferData(a, b.isEmpty() ? ItemStack.EMPTY : b, s));

        // clear 3 slot
        offerInv.setItem(0, ItemStack.EMPTY);
        offerInv.setItem(1, ItemStack.EMPTY);
        offerInv.setItem(2, ItemStack.EMPTY);

        this.broadcastChanges(); // sync lại cho client
    }
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
