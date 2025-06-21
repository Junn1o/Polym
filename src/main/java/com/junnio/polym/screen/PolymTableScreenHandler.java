package com.junnio.polym.screen;

import com.junnio.polym.recipe.ModRecipes;
import com.junnio.polym.recipe.PolymRecipe;
import com.junnio.polym.sound.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
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

        this.resultInventory = new CraftingResultInventory();

        this.craftingInventory = new CraftingInventory(this, 3, 3);

        this.addSlot(new Slot(this.resultInventory, 0, 124, 35) {
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                if (player.getWorld().isClient()) {
                    player.playSound(ModSounds.POLYM_ON_CRAFT, 1.0F, 1.0F);
                }
                consumeIngredients();
                updateRecipeOutput();
                super.onTakeItem(player, stack);
            }
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(this.craftingInventory, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

    }
    private void consumeIngredients() {
        for (int i = 0; i < craftingInventory.size(); i++) {
            ItemStack slotStack = craftingInventory.getStack(i);

            if (!slotStack.isEmpty()) {

                slotStack.decrement(1);

                if (slotStack.getCount() == 0) {
                    craftingInventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }

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

    public void updateRecipeOutput() {
        if (!this.world.isClient) {
            CraftingRecipeInput recipeInput = craftingInventory.createRecipeInput();

            Optional<RecipeEntry<PolymRecipe>> polymRecipe = this.world.getServer()
                    .getRecipeManager()
                    .getFirstMatch(ModRecipes.POLYM_CRAFTING_TYPE, recipeInput, this.world);

            if (polymRecipe.isPresent()) {
                ItemStack result = polymRecipe.get().value().craft(recipeInput, this.world.getRegistryManager());
                this.resultInventory.setStack(0, result);
                return;
            }

            Optional<RecipeEntry<CraftingRecipe>> vanillaRecipe = this.world.getServer()
                    .getRecipeManager()
                    .getFirstMatch(RecipeType.CRAFTING, recipeInput, this.world);

            if (vanillaRecipe.isPresent() && vanillaRecipe.get().id().getValue().getNamespace().equals("polym")) {
                ItemStack result = vanillaRecipe.get().value().craft(recipeInput, this.world.getRegistryManager());
                this.resultInventory.setStack(0, result);
                return;
            }

            this.resultInventory.setStack(0, ItemStack.EMPTY);
        }
    }
}