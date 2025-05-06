package com.junnio.polycoin.screen;

import com.junnio.polycoin.recipe.ModRecipes;
import com.junnio.polycoin.recipe.PolymCoinRecipe;
import com.junnio.polycoin.recipe.PolymToolUseRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

import java.util.Optional;

public class PolymTableScreenHandler extends ScreenHandler {
    private final World world;
    private final CraftingInventory craftingInventory;
    private final Inventory resultInventory;

    public PolymTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new CraftingInventory(null, 3, 3), new CraftingResultInventory());
        System.out.println("Creating PolymTableScreenHandler on " + (world.isClient ? "Client" : "Server"));
        System.out.println("Crafting inventory size: " + craftingInventory.size());
        System.out.println("Result inventory size: " + resultInventory.size());
    }

    public PolymTableScreenHandler(int syncId, PlayerInventory playerInventory, CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
        super(ModScreenHandlers.POLYM_TABLE_SCREEN_HANDLER, syncId);
        this.craftingInventory = craftingInventory;
        this.resultInventory = resultInventory;
        this.world = playerInventory.player.getWorld();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(craftingInventory, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        this.addSlot(new Slot(resultInventory, 0, 124, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // Prevent manual insertion
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                // Call onCrafted() when items are crafted
                stack.onCraftByPlayer(player.getWorld(), player, stack.getCount());
                onCrafted(stack, stack.getCount());
                super.onTakeItem(player, stack);
            }
        });


        // Add player inventory slots (your layout logic here)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) { // 27 slots
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }


        // Add player hotbar slots
        for (int col = 0; col < 9; col++) { // 9 slots
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }


    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true; // Logic to determine if player can access
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
        CraftingRecipeInput recipeInput = craftingInventory.createRecipeInput();
        Optional<RecipeEntry<PolymToolUseRecipe>> optional = this.world.getServer().getRecipeManager()
                .getFirstMatch(ModRecipes.POLYM_CRAFTING_TYPE, recipeInput, this.world);

        if (optional.isPresent()) {
            PolymToolUseRecipe recipe = optional.get().value();
            this.resultInventory.setStack(0, recipe.craft(recipeInput, null)); // Set crafted output
        } else {
            this.resultInventory.setStack(0, ItemStack.EMPTY); // No valid recipe
        }
    }






}