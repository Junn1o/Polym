package com.junnio.polycoin.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class PolymCraftingRecipe implements Recipe<PolymTableRecipeInput> {
    private final Identifier id;
    private final Ingredient input;
    private final ItemStack output;

    public PolymCraftingRecipe(Identifier id, Ingredient input, ItemStack output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(PolymTableRecipeInput input, World world) {
        return this.input.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(PolymTableRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return false;
    }

    @Override
    public RecipeSerializer<? extends Recipe<PolymTableRecipeInput>> getSerializer() {
        return ModRecipes.POLYM_CRAFTING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<PolymTableRecipeInput>> getType() {
        return ModRecipes.POLYM_CRAFTING_TYPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forSingleSlot(input);
    }

    @Override
    public List<RecipeDisplay> getDisplays() {
        return List.of();
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
    }
    public Identifier getId() {
        return id;
    }
    public Ingredient getInput() {
        return this.input;
    }

    public ItemStack getOutput() {
        return this.output;
    }
}
