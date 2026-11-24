package com.junnio.polym.screen;

import com.junnio.polym.command.ModCommand;
import com.junnio.polym.recipe.ModRecipes;
import com.junnio.polym.recipe.PolymRecipe;
import com.junnio.polym.sound.ModSounds;
import com.junnio.polym.util.CraftAuditLogger;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class PolymTableScreenHandler extends AbstractContainerMenu {
    private final Level world;
    private final TransientCraftingContainer craftingInventory;
    private final Container resultInventory;
    private final Player playerentity;
    private RecipeHolder<PolymRecipe> currentPolymRecipe;
    private RecipeHolder<CraftingRecipe> currentVanillaRecipe;

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
                if (player instanceof ServerPlayer sp && currentPolymRecipe != null) {
                    ItemStack resultToLog = currentPolymRecipe.value()
                            .assemble(craftingInventory.asCraftInput(), world.registryAccess());
                    CraftAuditLogger.logPolymCraft(sp, currentPolymRecipe.id().identifier(), resultToLog);
                }

                consumeIngredients(player);
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
    private void consumeIngredients(Player player) {
        RecipeHolder<CraftingRecipe> vanilla = currentVanillaRecipe;
        CraftingInput.Positioned positioned = craftingInventory.asPositionedCraftInput();
        NonNullList<ItemStack> remainders = vanilla == null
                ? null
                : vanilla.value().getRemainingItems(positioned.input());

        for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
            ItemStack slotStack = craftingInventory.getItem(i);

            if (!slotStack.isEmpty()) {

                slotStack.shrink(1);

                if (slotStack.getCount() == 0) {
                    craftingInventory.setItem(i, ItemStack.EMPTY);
                }
            }
        }

        if (remainders != null) {
            int width = positioned.input().width();
            for (int i = 0; i < remainders.size(); i++) {
                ItemStack remainder = remainders.get(i);
                if (remainder.isEmpty()) continue;

                int slot = (i % width + positioned.left()) + (i / width + positioned.top()) * craftingInventory.getWidth();
                ItemStack current = craftingInventory.getItem(slot);
                if (current.isEmpty()) {
                    craftingInventory.setItem(slot, remainder);
                } else if (ItemStack.isSameItemSameComponents(current, remainder)) {
                    remainder.grow(current.getCount());
                    craftingInventory.setItem(slot, remainder);
                } else if (!player.getInventory().add(remainder)) {
                    player.drop(remainder, false);
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
        if (!player.level().isClientSide()) {
            for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
                ItemStack stack = craftingInventory.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
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
            this.currentPolymRecipe = null;
            this.currentVanillaRecipe = null;
            CraftingInput recipeInput = craftingInventory.asCraftInput();
            if (recipeInput.isEmpty()) {
                this.resultInventory.setItem(0, ItemStack.EMPTY);
                return;
            }
            RecipeManager recipes = this.world.getServer().getRecipeManager();
            var polymOpt = recipes.getRecipeFor(ModRecipes.POLYM_CRAFTING_TYPE, recipeInput, this.world);

            if (polymOpt.isPresent()) {
                var holder = polymOpt.get();
                if (!playerentity.getTags().contains(ModCommand.GUILD_TAG)
                        && holder.id().identifier().getPath().startsWith("guild_")) {
                    this.resultInventory.setItem(0, ItemStack.EMPTY);
                    return;
                }
                this.currentPolymRecipe = holder;
                this.resultInventory.setItem(0, holder.value().assemble(recipeInput, this.world.registryAccess()));
                return;
            }

            var vanillaOpt = recipes.getRecipeFor(RecipeType.CRAFTING, recipeInput, this.world);

            if (vanillaOpt.isPresent()) {
                var holder = vanillaOpt.get();
                this.currentVanillaRecipe = holder;
                this.resultInventory.setItem(0, holder.value().assemble(recipeInput, this.world.registryAccess()));
                return;
            }
            this.resultInventory.setItem(0, ItemStack.EMPTY);
        }
    }
}
