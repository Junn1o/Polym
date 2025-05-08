package com.junnio.polym.recipe;

import com.junnio.polym.Polym;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static void initialize(){

        Polym.LOGGER.info("Registering Custom Recipes for " + Polym.MOD_ID);
    }

public static final RecipeSerializer<PolymRecipe> TOOL_USE_RECIPE_SERIALIZER = Registry.register(
        Registries.RECIPE_SERIALIZER, Identifier.of(Polym.MOD_ID, "polym_recipe"),
        new PolymRecipe.Serializer());
    public static final RecipeType<PolymRecipe> POLYM_CRAFTING_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Polym.MOD_ID, "polym_recipe"), new RecipeType<PolymRecipe>() {
                @Override
                public String toString() {
                    return "tool_use";
                }
            });
}
