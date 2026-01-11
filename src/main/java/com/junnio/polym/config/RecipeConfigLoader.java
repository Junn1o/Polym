package com.junnio.polym.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.junnio.polym.Polym;
import com.junnio.polym.recipe.PolymRecipe;
import com.junnio.polym.recipe.PolymRecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads Polym Table-only recipes from config/polym/recipes/*.json.
 */
public final class RecipeConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = Paths.get("config", Polym.MOD_ID, "recipes");

    private RecipeConfigLoader() {}

    /**
     * Creates an example JSON file on first run to show players the expected format.
     */
    public static void createExampleRecipeIfMissing() {
        try {
            Files.createDirectories(CONFIG_DIR);

            Path example = CONFIG_DIR.resolve("example_copper_coin.json");
            if (Files.exists(example)) return;

            JsonObject root = new JsonObject();
            root.addProperty("id", "example_copper_coin");

            JsonArray pattern = new JsonArray();
            pattern.add("   ");
            pattern.add(" C ");
            pattern.add("   ");
            root.add("pattern", pattern);

            JsonObject key = new JsonObject();
            key.addProperty("C", "minecraft:copper_ingot");
            root.add("key", key);

            JsonObject result = new JsonObject();
            result.addProperty("item", Polym.MOD_ID + ":copper_coin");
            result.addProperty("count", 1);
            root.add("result", result);

            Files.writeString(example, GSON.toJson(root));
            Polym.LOGGER.info("Created example Polym recipe config: {}", example.toAbsolutePath());
        } catch (IOException e) {
            Polym.LOGGER.error("Failed to create example Polym recipe config: {}", e.getMessage());
        }
    }

    /**
     * Reloads all recipes from disk into PolymRecipeRegistry.
     */
    public static void loadAllRecipes() {
        PolymRecipeRegistry.clear();

        int loaded = 0;
        try {
            Files.createDirectories(CONFIG_DIR);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(CONFIG_DIR, "*.json")) {
                for (Path p : stream) {
                    try {
                        loadRecipeFile(p);
                        loaded++;
                    } catch (Exception ex) {
                        Polym.LOGGER.error("Failed to load recipe config {}: {}", p.getFileName(), ex.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            Polym.LOGGER.error("Failed to read recipe config directory {}: {}", CONFIG_DIR.toAbsolutePath(), e.getMessage());
        }

        Polym.LOGGER.info("Loaded {} Polym Table config recipes", loaded);
    }

    private static void loadRecipeFile(Path path) throws IOException {
        JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();

        String id = requireString(root, "id");
        String[] pattern = parsePattern(root);
        Map<String, Ingredient> key = parseKey(root);
        ItemStack result = parseResult(root);

        PolymRecipe recipe = new PolymRecipe(pattern, key, result);
        PolymRecipeRegistry.register(id, recipe);
    }

    private static String[] parsePattern(JsonObject root) {
        if (!root.has("pattern") || !root.get("pattern").isJsonArray()) {
            throw new JsonSyntaxException("Missing or invalid 'pattern' array");
        }
        JsonArray arr = root.getAsJsonArray("pattern");
        String[] out = new String[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            out[i] = arr.get(i).getAsString();
        }
        return out;
    }

    private static Map<String, Ingredient> parseKey(JsonObject root) {
        if (!root.has("key") || !root.get("key").isJsonObject()) {
            throw new JsonSyntaxException("Missing or invalid 'key' object");
        }

        JsonObject obj = root.getAsJsonObject("key");
        Map<String, Ingredient> map = new HashMap<>();

        for (String symbol : obj.keySet()) {
            if (symbol.length() != 1) {
                throw new JsonSyntaxException("Key symbol must be a single character, got: " + symbol);
            }

            String itemId = obj.get(symbol).getAsString();
            Identifier id = Identifier.of(itemId);
            if (!Registries.ITEM.containsId(id)) {
                throw new JsonSyntaxException("Unknown item in key: " + itemId);
            }

            map.put(symbol, Ingredient.ofItems(Registries.ITEM.get(id)));
        }

        return map;
    }

    private static ItemStack parseResult(JsonObject root) {
        if (!root.has("result") || !root.get("result").isJsonObject()) {
            throw new JsonSyntaxException("Missing or invalid 'result' object");
        }

        JsonObject obj = root.getAsJsonObject("result");
        String itemId = requireString(obj, "item");
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;

        Identifier id = Identifier.of(itemId);
        if (!Registries.ITEM.containsId(id)) {
            throw new JsonSyntaxException("Unknown result item: " + itemId);
        }

        return new ItemStack(Registries.ITEM.get(id), Math.max(1, count));
    }

    private static String requireString(JsonObject obj, String field) {
        if (!obj.has(field)) {
            throw new JsonSyntaxException("Missing required field: " + field);
        }
        return obj.get(field).getAsString();
    }
}
