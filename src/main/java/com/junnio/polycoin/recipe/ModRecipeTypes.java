package com.junnio.polycoin.recipe;

import com.junnio.polycoin.PoLymCoin;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeTypes {
    public static final RecipeType<PolymCraftingRecipe> POLYM_CRAFTING =
            Registry.register(Registries.RECIPE_TYPE, Identifier.of("polymcoin", "polym_crafting"), new RecipeType<PolymCraftingRecipe>() {
                public String toString() {
                    return "polymcoin:polym_crafting";
                }
            });
    public static void registerRecipes() {
        PoLymCoin.LOGGER.info("Registering Custom Recipes for " + PoLymCoin.MOD_ID);
    }
}
