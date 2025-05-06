package com.junnio.polycoin.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public record PolymToolUseRecipe(Ingredient baseItem, Ingredient tool, ItemStack result) implements Recipe<CraftingRecipeInput> {

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return baseItem.test(input.getStackInSlot(0)) && tool.test(input.getStackInSlot(1));
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<CraftingRecipeInput>> getSerializer() {
        return ModRecipes.TOOL_USE_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<CraftingRecipeInput>> getType() {
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

    public static class Serializer implements RecipeSerializer<PolymToolUseRecipe> {
        public static final MapCodec<PolymToolUseRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("item").forGetter(PolymToolUseRecipe::baseItem),
                Ingredient.CODEC.fieldOf("tool").forGetter(PolymToolUseRecipe::tool),
                ItemStack.CODEC.fieldOf("result").forGetter(PolymToolUseRecipe::result)
        ).apply(inst, PolymToolUseRecipe::new));

        public static final PacketCodec<RegistryByteBuf, PolymToolUseRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, PolymToolUseRecipe::baseItem,
                        Ingredient.PACKET_CODEC, PolymToolUseRecipe::tool,
                        ItemStack.PACKET_CODEC, PolymToolUseRecipe::result,
                        PolymToolUseRecipe::new);

        @Override
        public MapCodec<PolymToolUseRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PolymToolUseRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
