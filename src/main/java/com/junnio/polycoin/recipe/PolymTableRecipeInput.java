package com.junnio.polycoin.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;

public class PolymTableRecipeInput implements RecipeInput{
    private final DefaultedList<ItemStack> inputs;

    public PolymTableRecipeInput(DefaultedList<ItemStack> inputs) {
        this.inputs = inputs;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inputs.get(slot);
    }

    @Override
    public int size() {
        return inputs.size();
    }

    public DefaultedList<ItemStack> getInputs() {
        return inputs;
    }
}
