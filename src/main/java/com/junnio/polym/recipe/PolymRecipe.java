package com.junnio.polym.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.*;

public record PolymRecipe(String[] pattern, Map<String, Ingredient> key, ItemStack result) implements Recipe<CraftingInput> {

    @Override
    public boolean matches(CraftingInput input, Level world) {
        if (input.width() != 3 || input.height() != 3) {
            return false;
        }

        for (int i = 0; i <= input.width() - pattern[0].length(); ++i) {
            for (int j = 0; j <= input.height() - pattern.length; ++j) {
                if (matches(input, i, j, true) || matches(input, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider) {
        return result.copy();
    }
    private boolean matches(CraftingInput inv, int offsetX, int offsetY, boolean flipped) {
        for (int i = 0; i < inv.width(); i++) {
            for (int j = 0; j < inv.height(); j++) {
                int x = i - offsetX;
                int y = j - offsetY;

                String symbolStr = " ";
                if (x >= 0 && y >= 0 && x < pattern[0].length() && y < pattern.length) {
                    char symbol = flipped ?
                            pattern[y].charAt(pattern[0].length() - 1 - x) :
                            pattern[y].charAt(x);
                    symbolStr = String.valueOf(symbol);
                }

                ItemStack stackInSlot = inv.getItem(i + j * inv.width());
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
    public RecipeSerializer<? extends Recipe<CraftingInput>> getSerializer() {
        return ModRecipes.RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<CraftingInput>> getType() {
        return ModRecipes.POLYM_CRAFTING_TYPE;
    }

    @Override
    public PlacementInfo placementInfo() {
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
        return PlacementInfo.createFromOptionals(ingredients);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }


    public static class Serializer implements RecipeSerializer<PolymRecipe> {
        public static final MapCodec<PolymRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.listOf().fieldOf("pattern").xmap(list -> list.toArray(new String[0]), Arrays::asList)
                        .forGetter(r -> r.pattern),

                Codec.unboundedMap(Codec.STRING, Ingredient.CODEC)
                        .fieldOf("key")
                        .forGetter(PolymRecipe::key),

                ItemStack.CODEC.fieldOf("result").forGetter(PolymRecipe::result)
        ).apply(inst, PolymRecipe::new));


        @Override
        public MapCodec<PolymRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PolymRecipe> streamCodec() {
            return null;
        }
    }
}
