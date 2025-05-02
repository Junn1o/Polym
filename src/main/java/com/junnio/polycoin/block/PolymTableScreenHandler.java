package com.junnio.polycoin.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

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
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
        CraftingRecipeInput craftingRecipeInput = craftingInventory.createRecipeInput();
        resultInventory.setStack(0, itemStack);
        handler.setPreviousTrackedSlot(0, itemStack);
        Optional<RecipeEntry<CraftingRecipe>> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, world, recipe);
        if (optional.isPresent()) {
            RecipeEntry<CraftingRecipe> recipeEntry = (RecipeEntry<CraftingRecipe>)optional.get();
            CraftingRecipe craftingRecipe = recipeEntry.value();
            if (resultInventory.shouldCraftRecipe(serverPlayerEntity, recipeEntry)) {
                ItemStack itemStack2 = craftingRecipe.craft(craftingRecipeInput, world.getRegistryManager());
                if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
                    itemStack = itemStack2;
                }
            }
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(
                    new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack)
            );
        }
    }
}
