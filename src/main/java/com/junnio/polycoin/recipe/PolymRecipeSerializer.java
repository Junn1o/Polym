package com.junnio.polycoin.recipe;

import com.junnio.polycoin.PoLymCoin;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class PolymRecipeSerializer implements RecipeSerializer<PolymTableCraftingRecipe> {


    public static final MapCodec<PolymTableCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input1").forGetter(PolymTableCraftingRecipe::getInputA),
            Ingredient.CODEC.fieldOf("input2").forGetter(PolymTableCraftingRecipe::getInputB),
            ItemStack.CODEC.fieldOf("result").forGetter(PolymTableCraftingRecipe::getOutputStack)
    ).apply(instance, PolymTableCraftingRecipe::new));
    private PolymRecipeSerializer() {

    }
    public static final PolymRecipeSerializer INSTANCE = new PolymRecipeSerializer();
    public static final Identifier ID = Identifier.of(PoLymCoin.MOD_ID + ":test");
    @Override
    public MapCodec<PolymTableCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, PolymTableCraftingRecipe> packetCodec() {
        return null;
    }
}
