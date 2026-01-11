package com.junnio.polym.recipe;

import com.junnio.polym.Polym;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.world.World;

import java.util.*;

/**
 * In-memory registry for Polym Table recipes loaded from config files.
 * These recipes are used only by the Polym Table and are NOT registered in Minecraft's RecipeManager.
 */
public final class PolymRecipeRegistry {
    private static final Map<String, PolymRecipe> RECIPES = new LinkedHashMap<>();

    private PolymRecipeRegistry() {}

    public static void register(String id, PolymRecipe recipe) {
        RECIPES.put(id, recipe);
        Polym.LOGGER.info("Registered Polym config recipe: {}", id);
    }

    public static Optional<PolymRecipe> getFirstMatch(CraftingRecipeInput input, World world) {
        return RECIPES.values().stream()
                .filter(r -> r.matches(input, world))
                .findFirst();
    }

    public static void clear() {
        RECIPES.clear();
    }

    public static int size() {
        return RECIPES.size();
    }

    public static Collection<PolymRecipe> all() {
        return Collections.unmodifiableCollection(RECIPES.values());
    }
}
