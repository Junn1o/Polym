package com.junnio.polycoin.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class PolymCraftingRecipeSerializer implements RecipeSerializer<PolymCraftingRecipe> {
    public static final MapCodec<PolymCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(recipe -> recipe.getId()),
            Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInput()),
            ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput())
    ).apply(instance, PolymCraftingRecipe::new));

    @Override
    public MapCodec<PolymCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, PolymCraftingRecipe> packetCodec() {
        return null;
    }
}
