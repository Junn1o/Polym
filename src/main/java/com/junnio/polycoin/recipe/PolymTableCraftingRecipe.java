package com.junnio.polycoin.recipe;

import com.junnio.polycoin.PoLymCoin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class PolymTableCraftingRecipe implements Recipe<CraftingRecipeInput> {

    private final Ingredient inputA;
    private final Ingredient inputB;
    private final ItemStack outputStack;
    public PolymTableCraftingRecipe(Ingredient inputA, Ingredient inputB, ItemStack outputStack) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.outputStack = outputStack;
    }
    public Ingredient getInputA() {
        return inputA;
    }

    public Ingredient getInputB() {
        return inputB;
    }
    public ItemStack getOutputStack() {
        return outputStack;
    }
    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (input.size() < 2) return false;
        return inputA.equals(input.getStackInSlot(0)) && inputB.equals(input.getStackInSlot(1));
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return outputStack.copy();
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return Recipe.super.isIgnoredInRecipeBook();
    }

    @Override
    public RecipeSerializer<? extends Recipe<CraftingRecipeInput>> getSerializer() {
        return PolymRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<CraftingRecipeInput>> getType() {
        return Type.INSTANCE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return null;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
    }
    public static class Type implements RecipeType<PolymTableCraftingRecipe> {
        // Define ExampleRecipe.Type as a singleton by making its constructor private and exposing an instance.
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "two_slot_recipe";
    }
}
