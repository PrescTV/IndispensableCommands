package net.presc.indispensablecommands.data;

import com.google.gson.*;
import net.presc.indispensablecommands.IndispensableCommands;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LanguageConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = IndispensableCommands.getConfigDir().resolve("lang.json");
    private static Map<UUID, String> playerLanguages = new HashMap<>();

    public static void load() {
        try {
            File configFile = CONFIG_PATH.toFile();
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                return;
            }

            try (Reader reader = new FileReader(configFile)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    try {
                        UUID playerId = UUID.fromString(entry.getKey());
                        String language = entry.getValue().getAsString();
                        playerLanguages.put(playerId, language);
                    } catch (IllegalArgumentException e) {
                        IndispensableCommands.LOGGER.warn("Invalid UUID in language config: " + entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            IndispensableCommands.LOGGER.error("Failed to load language config", e);
        }
    }

    public static void save() {
        try {
            File configFile = CONFIG_PATH.toFile();
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }

            JsonObject json = new JsonObject();
            for (Map.Entry<UUID, String> entry : playerLanguages.entrySet()) {
                json.addProperty(entry.getKey().toString(), entry.getValue());
            }

            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(json, writer);
            }
        } catch (Exception e) {
            IndispensableCommands.LOGGER.error("Failed to save language config", e);
        }
    }

    public static void setLanguage(UUID playerId, String language) {
        playerLanguages.put(playerId, language);
        save();
    }

    public static String getLanguageOrDefault(UUID playerId, String defaultLanguage) {
        return playerLanguages.getOrDefault(playerId, defaultLanguage);
    }
}