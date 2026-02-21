package com.junnio.polym.screen;

import com.junnio.polym.net.ShopOfferData;
import com.junnio.polym.net.ShopOpenData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ShopScreenHandler extends AbstractContainerMenu {
    private final List<ShopOfferData> offers;
    public List<ShopOfferData> getOffers() {
        return List.copyOf(offers);
    }

    public static final int SLOT_BUY_A = 0;
    public static final int SLOT_BUY_B = 1;
    public static final int SLOT_SELL  = 2;
    public ShopScreenHandler(int syncId, Inventory playerInventory, Player player, ShopOpenData data) {
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER, syncId);
        Level world = playerInventory.player.level();
        this.offers = new ArrayList<>(data.offers());
        Container offerInv = new SimpleContainer(3);
        this.addSlot(new Slot(offerInv, SLOT_BUY_A,  /*x*/ 136, /*y*/ 37));
        this.addSlot(new Slot(offerInv, SLOT_BUY_B,  /*x*/ 162, /*y*/ 37));
        this.addSlot(new Slot(offerInv, SLOT_SELL,   /*x*/ 220, /*y*/ 37));
        this.addStandardInventorySlots(playerInventory, 108, 84);
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
    public boolean stillValid(Player player) {
        return true;
    }
}
