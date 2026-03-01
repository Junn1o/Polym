package com.junnio.polym.screen;

import com.junnio.polym.net.shop.AllShopOpenData;
import com.junnio.polym.net.shop.ShopOfferViewData;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ShopScreenHandler extends AbstractContainerMenu {
    private final List<ShopOfferViewData> offers;
    public List<ShopOfferViewData> getOffers() {
        return List.copyOf(offers);
    }
    private final Container offerInv = new SimpleContainer(5);
    public static final int SLOT_BUY_A = 0;
    public static final int SLOT_BUY_B = 1;
    public static final int SLOT_BUY_C = 2;
    public static final int SLOT_SELL  = 3;
    public static final int SLOT_SELL_B  = 4;
    public ShopScreenHandler(int syncId, Inventory playerInventory, Player player, AllShopOpenData data) {
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER, syncId);
        this.offers = new ArrayList<>(data.offers());
        this.addSlot(new Slot(offerInv, SLOT_BUY_A,  110, 37));
        this.addSlot(new Slot(offerInv, SLOT_BUY_B,  136, 37));
        this.addSlot(new Slot(offerInv, SLOT_BUY_C,  162, 37));
        this.addSlot(new Slot(offerInv, SLOT_SELL,   220, 37));
        this.addSlot(new Slot(offerInv, SLOT_SELL_B,   246, 37));
        this.addStandardInventorySlots(playerInventory, 108, 84);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            for (int s = 0; s < this.offerInv.getContainerSize(); s++) {
                ItemStack stack = this.offerInv.getItem(s);
                if (!stack.isEmpty()) {
                    if (!player.getInventory().add(stack.copy())) {
                        player.drop(stack.copy(), false);
                    }
                    this.offerInv.setItem(s, ItemStack.EMPTY);
                }
            }
            this.offerInv.setChanged();
        }
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
    public boolean stillValid(Player player) {
        return true;
    }
}
