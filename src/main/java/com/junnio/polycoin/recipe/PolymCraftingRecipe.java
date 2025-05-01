package com.junnio.polycoin.recipe;

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

public record PolymCraftingRecipe(Ingredient inputItem, ItemStack output) implements Recipe<PolymTableRecipeInput> {

    @Override
    public boolean matches(PolymTableRecipeInput input, World world) {
        if(world.isClient()) {
            return false;
        }

        return inputItem.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(PolymTableRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.copy();
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
        return null;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
    }
    public static class Serializer implements RecipeSerializer<PolymCraftingRecipe> {

        public static final MapCodec<PolymCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(PolymCraftingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(PolymCraftingRecipe::output)
        ).apply(inst, PolymCraftingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, PolymCraftingRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, PolymCraftingRecipe::inputItem,
                        ItemStack.PACKET_CODEC, PolymCraftingRecipe::output,
                        PolymCraftingRecipe::new);
        @Override
        public MapCodec<PolymCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PolymCraftingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
