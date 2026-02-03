package com.junnio.polym.recipe;

import com.junnio.polym.Polym;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {
    public static void initialize(){
    }

public static final RecipeSerializer<PolymRecipe> RECIPE_SERIALIZER = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(Polym.MOD_ID, "polym_recipe"),
        new PolymRecipe.Serializer());
    public static final RecipeType<PolymRecipe> POLYM_CRAFTING_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(Polym.MOD_ID, "polym_recipe"), new RecipeType<PolymRecipe>() {});
}
