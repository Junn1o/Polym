package com.junnio.polycoin.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public record PolymTableCraftingRecipe (Ingredient inputItem, ItemStack output) implements Recipe<PolymTableCraftingRecipeInput> {
    @Override
    public boolean matches(PolymTableCraftingRecipeInput input, World world) {
        if (!(input instanceof PolymTableCraftingRecipeInput)) {
            return false;
        }
        if(world.isClient()) {
            return false;
        }
        return inputItem.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(PolymTableCraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<PolymTableCraftingRecipeInput>> getSerializer() {
        return ModRecipes.POLYM_CRAFTING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<PolymTableCraftingRecipeInput>> getType() {
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

    public static class Serializer implements RecipeSerializer<PolymTableCraftingRecipe> {
        public static final MapCodec<PolymTableCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(PolymTableCraftingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(PolymTableCraftingRecipe::output)
        ).apply(inst, PolymTableCraftingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, PolymTableCraftingRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, PolymTableCraftingRecipe::inputItem,
                        ItemStack.PACKET_CODEC, PolymTableCraftingRecipe::output,
                        PolymTableCraftingRecipe::new);

        @Override
        public MapCodec<PolymTableCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PolymTableCraftingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
