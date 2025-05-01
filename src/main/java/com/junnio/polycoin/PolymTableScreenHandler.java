package com.junnio.polycoin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PolymTableScreenHandler extends CraftingScreenHandler {
    private final PlayerEntity customPlayer;
    private final ScreenHandlerContext customContext;
    public PolymTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(syncId, playerInventory, context);
        this.customPlayer = playerInventory.player;
        this.customContext = context;
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    @Override
    public void onContentChanged(Inventory inventory) {
        customContext.run((world, pos) -> {
            if (world instanceof ServerWorld serverWorld) {
                updateResult(this, serverWorld, customPlayer, this.craftingInventory, this.craftingResultInventory, null);
            }
        });
    }

    // This method overrides vanilla result crafting
    public static void updateResult(ScreenHandler handler, ServerWorld world, PlayerEntity player,
                                    RecipeInputInventory craftingInventory,
                                    CraftingResultInventory resultInventory,
                                    RecipeEntry<CraftingRecipe> recipe) {
        ItemStack itemStack = ItemStack.EMPTY;
        resultInventory.setStack(0, itemStack);
        handler.setPreviousTrackedSlot(0, itemStack);

        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(
                    new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack)
            );
        }
    }
}
