package net.presc.indispensablecommands.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.presc.indispensablecommands.IndispensableCommands;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "indispensable_homes.json";

    public static void saveHomes(MinecraftServer server, Map<UUID, Map<String, Home>> homes) {
        Path savePath = server.getSavePath(WorldSavePath.ROOT).resolve(FILE_NAME);
        try (Writer writer = new FileWriter(savePath.toFile())) {
            GSON.toJson(homes, writer);
        } catch (IOException e) {
            IndispensableCommands.LOGGER.error("Failed to save homes data", e);
        }
    }

    public static Map<UUID, Map<String, Home>> loadHomes(MinecraftServer server) {
        Path savePath = server.getSavePath(WorldSavePath.ROOT).resolve(FILE_NAME);
        File file = savePath.toFile();

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<UUID, Map<String, Home>>>(){}.getType();
            Map<UUID, Map<String, Home>> loadedHomes = GSON.fromJson(reader, type);

            // Initialiser les dimensions pour tous les homes
            loadedHomes.forEach((uuid, homes) ->
                    homes.forEach((name, home) -> home.initDimension(server))
            );

            return loadedHomes;
        } catch (IOException e) {
            IndispensableCommands.LOGGER.error("Failed to load homes data", e);
            return new HashMap<>();
        }
    }
}