package com.junnio.polym.screen;

import com.junnio.polym.recipe.ModRecipes;
import com.junnio.polym.recipe.PolymRecipe;
import com.junnio.polym.sound.ModSounds;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class PolymTableScreenHandler extends AbstractContainerMenu {
    private final Level world;
    private final TransientCraftingContainer craftingInventory;
    private final Container resultInventory;
    private final Player playerentity;
    public PolymTableScreenHandler(int syncId, Inventory playerInventory, Player playerentity) {
        super(ModScreenHandlers.POLYM_TABLE_SCREEN_HANDLER, syncId);

        this.world = playerInventory.player.level();
        this.playerentity = playerentity;

        this.resultInventory = new ResultContainer();

        this.craftingInventory = new TransientCraftingContainer(this, 3, 3);

        this.addSlot(new Slot(this.resultInventory, 0, 124, 35) {
            @Override
            public void onTake(Player player, ItemStack stack) {
                if (player.level().isClientSide()) {
                    player.playSound(ModSounds.POLYM_ON_CRAFT, 1.0F, 1.0F);
                }
                consumeIngredients();
                updateRecipeOutput();
                super.onTake(player, stack);
            }
            @Override
            public boolean mayPlace(ItemStack stack) {
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
        for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
            ItemStack slotStack = craftingInventory.getItem(i);

            if (!slotStack.isEmpty()) {

                slotStack.shrink(1);

                if (slotStack.getCount() == 0) {
                    craftingInventory.setItem(i, ItemStack.EMPTY);
                }
            }
        }

        craftingInventory.setChanged();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (slotIndex == 0) {
                if (!this.moveItemStackTo(slotStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemStack);
            }
            else if (slotIndex >= 10 && slotIndex < 46) {
                if (!this.moveItemStackTo(slotStack, 1, 10, false)) {
                    if (slotIndex < 37) {
                        if (!this.moveItemStackTo(slotStack, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(slotStack, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            else if (!this.moveItemStackTo(slotStack, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
            ItemStack itemStack = craftingInventory.removeItemNoUpdate(i);
            if (!itemStack.isEmpty()) {
                player.getInventory().add(itemStack);
                if (!itemStack.isEmpty()) {
                    player.drop(itemStack, false);
                }
            }
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
    public void slotsChanged(Container inventory) {
        if (inventory == craftingInventory) {
            updateRecipeOutput();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void updateRecipeOutput() {
        if (!this.world.isClientSide()) {
            CraftingInput recipeInput = craftingInventory.asCraftInput();
            Optional<RecipeHolder<PolymRecipe>> polymRecipe = this.world.getServer()
                    .getRecipeManager()
                    .getRecipeFor(ModRecipes.POLYM_CRAFTING_TYPE, recipeInput, this.world);

            if (polymRecipe.isPresent()) {
                if(!playerentity.getTags().contains("Guild") && polymRecipe.get().id().identifier().getPath().startsWith("guild_"))
                    return;
                ItemStack result = polymRecipe.get().value().assemble(recipeInput, this.world.registryAccess());
                this.resultInventory.setItem(0, result);
                return;
            }

            Optional<RecipeHolder<CraftingRecipe>> vanillaRecipe = this.world.getServer()
                    .getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, recipeInput, this.world);

            if (vanillaRecipe.isPresent()) {
                if (vanillaRecipe.get().id().identifier().getNamespace().equals("polym")) {
                    ItemStack result = vanillaRecipe.get().value().assemble(recipeInput, this.world.registryAccess());
                    this.resultInventory.setItem(0, result);
                    return;
                }
            }
            this.resultInventory.setItem(0, ItemStack.EMPTY);
        }
    }
}