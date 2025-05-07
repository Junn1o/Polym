package com.junnio.polycoin.screen;

import com.junnio.polycoin.recipe.ModRecipes;
import com.junnio.polycoin.recipe.PolymToolUseRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

public class PolymTableScreenHandler extends ScreenHandler {
    private final World world;
    private final CraftingInventory craftingInventory;
    private final Inventory resultInventory;

    public PolymTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.POLYM_TABLE_SCREEN_HANDLER, syncId);

        this.world = playerInventory.player.getWorld();

        // Initialize the result inventory (output slot)
        this.resultInventory = new CraftingResultInventory();

        // Initialize the crafting inventory (input slots), pass 'this' AFTER the super() call
        this.craftingInventory = new CraftingInventory(this, 3, 3);

        // Set up slots
        this.addSlot(new Slot(this.resultInventory, 0, 124, 35) {
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                // Reduce ingredients when the crafted item is taken from the output slot
                consumeIngredients();
                super.onTakeItem(player, stack);
            }
        });

        // Add crafting grid slots (input)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(this.craftingInventory, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        // Add player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Add player hotbar slots
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

    }
    private void consumeIngredients() {
        // Iterate through the crafting grid
        for (int i = 0; i < craftingInventory.size(); i++) {
            ItemStack slotStack = craftingInventory.getStack(i);

            if (!slotStack.isEmpty()) { // If the slot is not empty
                // Reduce the stack count by 1
                slotStack.decrement(1);

                // If the stack is now empty, clear the slot
                if (slotStack.getCount() == 0) {
                    craftingInventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }

        // Notify the server and client that the inventory has changed
        craftingInventory.markDirty();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();

            if (slotIndex == 0) {
                if (!this.insertItem(slotStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(slotStack, itemStack);
            }
            // If clicking from player inventory, try to move to crafting grid
            else if (slotIndex >= 10 && slotIndex < 46) {
                if (!this.insertItem(slotStack, 1, 10, false)) {
                    if (slotIndex < 37) {
                        if (!this.insertItem(slotStack, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(slotStack, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            // If clicking from crafting grid, try to move to player inventory
            else if (!this.insertItem(slotStack, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return itemStack;

    }

    @Override
    public void onClosed(PlayerEntity player) {
        for (int i = 0; i < craftingInventory.size(); i++) {
            ItemStack itemStack = craftingInventory.removeStack(i);
            if (!itemStack.isEmpty()) {
                player.getInventory().insertStack(itemStack);
                if (!itemStack.isEmpty()) {
                    player.dropItem(itemStack, false);
                }
            }
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory == craftingInventory) {
            updateRecipeOutput();
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    private void onCrafted() {
        // Add logic to handle grid inputs (e.g., consume items)
        for (int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                itemStack.decrement(1); // Consume one item
            }
        }
    }

    public void updateRecipeOutput() {
        if (!this.world.isClient) {
            CraftingRecipeInput recipeInput = craftingInventory.createRecipeInput();
            Optional<RecipeEntry<PolymToolUseRecipe>> optional = Objects.requireNonNull(this.world.getServer())
                    .getRecipeManager()
                    .getFirstMatch(ModRecipes.POLYM_CRAFTING_TYPE, recipeInput, this.world);

            if (optional.isPresent()) {
                ItemStack result = optional.get().value().craft(recipeInput, null);
                PolymToolUseRecipe recipe = optional.get().value();
                this.resultInventory.setStack(0, result);
                //this.resultInventory.setStack(0, recipe.craft(recipeInput, null)); // Set crafted output
            } else {
                this.resultInventory.setStack(0, ItemStack.EMPTY); // No valid recipe
            }
        }
    }






}