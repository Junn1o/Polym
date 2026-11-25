package com.junnio.polym.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CraftAuditLogger {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path LOG_FILE;

    static {
        Path dir = FabricLoader.getInstance().getConfigDir().resolve("polym");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create polym config dir", e);
        }
        LOG_FILE = dir.resolve("craft_log.csv");

        if (!Files.exists(LOG_FILE)) {
            try (BufferedWriter w = Files.newBufferedWriter(LOG_FILE, StandardOpenOption.CREATE_NEW)) {
                w.write("time,playerName,uuid,recipeId,resultItemId,resultCount");
                w.newLine();
            } catch (IOException e) {
                throw new RuntimeException("Cannot create craft_log.csv", e);
            }
        }
    }

    public static void logPolymCraft(ServerPlayer player, Identifier recipeId, ItemStack result) {
        String time = LocalDateTime.now().format(FMT);
        String playerName = player.getName().getString();
        String uuid = player.getUUID().toString();
        String recipeStr = recipeId.toString();
        String resultItem = BuiltInRegistries.ITEM.getKey(result.getItem()).toString();
        int count = result.getCount();

        String line = String.join(",",
                escapeCsv(time),
                escapeCsv(playerName),
                escapeCsv(uuid),
                escapeCsv(recipeStr),
                escapeCsv(resultItem),
                String.valueOf(count)
        );

        try (BufferedWriter w = Files.newBufferedWriter(
                LOG_FILE,
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
        )) {
            w.write(line);
            w.newLine();
        } catch (IOException e) {
            System.err.println("[Polym] Failed to write craft log: " + e.getMessage());
        }
    }

    private static String escapeCsv(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
