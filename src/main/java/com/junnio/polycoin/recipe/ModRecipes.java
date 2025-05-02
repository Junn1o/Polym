package com.junnio.polycoin.recipe;

import com.junnio.polycoin.PoLymCoin;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static void initialize(){
        PoLymCoin.LOGGER.info("Registering Custom Recipes for " + PoLymCoin.MOD_ID);
    }
    public static final RecipeSerializer<PolymTableCraftingRecipe> POLYM_CRAFTING_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(PoLymCoin.MOD_ID, "polym_table"),
            new PolymTableCraftingRecipe.Serializer());
    public static final RecipeType<PolymTableCraftingRecipe> POLYM_CRAFTING_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(PoLymCoin.MOD_ID, "polym_table"), new RecipeType<PolymTableCraftingRecipe>() {
                @Override
                public String toString() {
                    return "polym_table";
                }
            });
}
