package com.junnio.polym.recipe;

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

import java.util.*;

public record PolymRecipe(String[] pattern, Map<String, Ingredient> key, ItemStack result) implements Recipe<CraftingRecipeInput> {

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (input.getWidth() != 3 || input.getHeight() != 3) {
            return false;
        }

        for (int i = 0; i <= input.getWidth() - pattern[0].length(); ++i) {
            for (int j = 0; j <= input.getHeight() - pattern.length; ++j) {
                if (matches(input, i, j, true) || matches(input, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(CraftingRecipeInput inv, int offsetX, int offsetY, boolean flipped) {
        for (int i = 0; i < inv.getWidth(); i++) {
            for (int j = 0; j < inv.getHeight(); j++) {
                int x = i - offsetX;
                int y = j - offsetY;

                String symbolStr = " ";
                if (x >= 0 && y >= 0 && x < pattern[0].length() && y < pattern.length) {
                    char symbol = flipped ?
                            pattern[y].charAt(pattern[0].length() - 1 - x) :
                            pattern[y].charAt(x);
                    symbolStr = String.valueOf(symbol);
                }

                ItemStack stackInSlot = inv.getStackInSlot(i + j * inv.getWidth());
                if (symbolStr.equals(" ")) {
                    if (!stackInSlot.isEmpty()) {
                        return false;
                    }
                } else {
                    Ingredient ingredient = key.get(symbolStr);
                    if (ingredient == null || !ingredient.test(stackInSlot)) {
                        return false;
                    }
                }
            }
        }
        return true;
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
        List<Optional<Ingredient>> ingredients = new ArrayList<>();
        int width = pattern[0].length();
        int height = pattern.length;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char symbol = pattern[y].charAt(x);
                if (symbol == ' ') {
                    // Skip empty slots entirely instead of adding empty ingredients
                    ingredients.add(Optional.empty());
                } else {
                    Ingredient ingredient = key.get(String.valueOf(symbol));
                    if (ingredient != null) {
                        ingredients.add(Optional.of(ingredient));
                    } else {
                        ingredients.add(Optional.empty());
                    }
                }
            }
        }
        return IngredientPlacement.forMultipleSlots(ingredients);
    }


    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
    }

    public static class Serializer implements RecipeSerializer<PolymRecipe> {
        private static final Codec<Ingredient> INGREDIENT_CODEC = Codec.STRING.xmap(
                str -> Ingredient.ofItems(net.minecraft.registry.Registries.ITEM.get(Identifier.of(str))),
                ingredient -> ingredient.getMatchingItems().findFirst()
                        .map(entry -> net.minecraft.registry.Registries.ITEM.getId(entry.value()).toString())
                        .orElse("minecraft:air")
        );

        public static final MapCodec<PolymRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(

                Codec.STRING.listOf().fieldOf("pattern").xmap(
                        list -> list.toArray(new String[0]),
                        Arrays::asList
                ).forGetter(recipe -> recipe.pattern),
                Codec.unboundedMap(Codec.STRING, INGREDIENT_CODEC).fieldOf("key").forGetter(PolymRecipe::key),
                ItemStack.CODEC.fieldOf("result").forGetter(PolymRecipe::result)
        ).apply(inst, PolymRecipe::new));

        @Override
        public MapCodec<PolymRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PolymRecipe> packetCodec() {
            return null;
        }
    }
}
