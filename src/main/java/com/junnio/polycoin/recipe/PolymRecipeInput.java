package com.junnio.polycoin.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class PolymRecipeInput implements RecipeInput {
    private final int width; // Width of the crafting grid
    private final int height;
    private final List<ItemStack> stacks;

    public PolymRecipeInput(int width, int height, List<ItemStack> stacks) {
        this.width = width;
        this.height = height;
        this.stacks = stacks;
    }
    public static PolymRecipeInput create(int width, int height, DefaultedList<ItemStack> stacks) {
        return new PolymRecipeInput(width, height, stacks);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
    }
    public ItemStack getStackAt(int x, int y) {
        int index = x + y * this.width;
        return getStackInSlot(index);
    }

    @Override
    public int size() {
        return stacks.size();
    }
    public boolean isEmpty() {
        for (ItemStack itemStack : stacks) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<ItemStack> getStacks() {
        return this.stacks;
    }
}
