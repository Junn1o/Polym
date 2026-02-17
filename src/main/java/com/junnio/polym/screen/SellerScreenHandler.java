package com.junnio.polym.screen;

import com.junnio.polym.net.ShopOfferData;
import com.junnio.polym.net.ShopOpenData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SellerScreenHandler extends AbstractContainerMenu {
    private final Level world;
    private final Player playerentity;
    private final List<ShopOfferData> offers;
    public List<ShopOfferData> getOffers() { return offers; }

    public SellerScreenHandler(int syncId, Inventory playerInventory, Player player, ShopOpenData data) {
        super(ModScreenHandlers.SELLER_SCREEN_HANDLER, syncId);
        this.world = playerInventory.player.level();
        this.playerentity = player;
        this.offers = data.offers();
        this.addStandardInventorySlots(playerInventory, 108, 84);
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
