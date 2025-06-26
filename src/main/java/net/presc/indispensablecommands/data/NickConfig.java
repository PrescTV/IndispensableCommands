package net.presc.indispensablecommands.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.presc.indispensablecommands.IndispensableCommands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class NickConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final List<String> blacklistedPatterns = new ArrayList<>();
    private int maxNameLength = 16;

    public NickConfig() {}

    public static NickConfig readOrCreate() {
        Path configPath = IndispensableCommands.getNickConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                return GSON.fromJson(json, NickConfig.class);
            }
        } catch (IOException e) {
            IndispensableCommands.LOGGER.error("Unable to read nickconfig.json", e);
        }

        NickConfig config = new NickConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Path configPath = IndispensableCommands.getNickConfigPath();
            Files.createDirectories(configPath.getParent());
            String json = GSON.toJson(this);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            IndispensableCommands.LOGGER.error("Error writing nickconfig.json", e);
        }
    }

    public boolean formattingEnabled() {
        return true;
    }

    public boolean displayAbovePlayer() {
        return true;
    }

    public boolean operatorsBypassRestrictions() {
        return false;
    }

    public int maxNameLength() {
        return maxNameLength;
    }

    public void setMaxNameLength(int length) {
        if (length >= 1 && length <= 64) {
            this.maxNameLength = length;
            save();
        }
    }

    public List<String> getBlacklistedPatterns() {
        return blacklistedPatterns;
    }

    public void addBlacklistedPattern(String regex) {
        if (!blacklistedPatterns.contains(regex)) {
            blacklistedPatterns.add(regex);
            save();
        }
    }

    public boolean removeBlacklistedPattern(String regex) {
        boolean removed = blacklistedPatterns.remove(regex);
        if (removed) {
            save();
        }
        return removed;
    }

    public void clearBlacklist() {
        blacklistedPatterns.clear();
        save();
    }

    public boolean nameBlacklisted(String name) {
        String normalizedInput = Normalizer.normalize(name.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        for (String patternStr : blacklistedPatterns) {
            String normalizedPattern = Normalizer.normalize(patternStr.toLowerCase(), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            Pattern pattern = Pattern.compile(normalizedPattern, Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(normalizedInput).find()) {
                return true;
            }
        }
        return false;
    }
}