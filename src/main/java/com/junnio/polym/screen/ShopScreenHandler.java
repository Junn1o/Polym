package com.junnio.polym.screen;

import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShopScreenHandler extends AbstractContainerMenu {
    private final Level world;
    private final Player playerentity;
    public ShopScreenHandler(int syncId, Inventory playerInventory, Player playerentity) {
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER, syncId);
        this.world = playerInventory.player.level();
        this.playerentity = playerentity;
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
