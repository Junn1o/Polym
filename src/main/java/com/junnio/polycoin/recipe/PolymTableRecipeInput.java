package com.junnio.polycoin.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record PolymTableRecipeInput(RecipeInput input) implements RecipeInput{
    @Override
    public ItemStack getStackInSlot(int slot) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
