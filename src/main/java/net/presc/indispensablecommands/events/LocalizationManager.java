package net.presc.indispensablecommands.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.presc.indispensablecommands.data.LanguageConfig;
import net.presc.indispensablecommands.IndispensableCommands;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocalizationManager {
    private static String defaultLanguage = "en_us";
    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    public static void initialize() {
        LanguageConfig.load();
        loadLanguageFile("en_us");
        loadLanguageFile("fr_fr");
        loadLanguageFile("ja_jp");
        loadLanguageFile("zh_cn");
        loadLanguageFile("es_es");
        loadLanguageFile("de_de");
        loadLanguageFile("hi_in");
    }

    private static void loadLanguageFile(String lang) {
        try {
            String path = String.format("assets/indispensable-commands/lang/%s.json", lang);
            InputStream stream = IndispensableCommands.class.getClassLoader().getResourceAsStream(path);

            if (stream != null) {
                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                Map<String, String> langMap = new HashMap<>();

                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    langMap.put(entry.getKey(), entry.getValue().getAsString());
                }

                translations.put(lang, langMap);
                stream.close();
            }
        } catch (Exception e) {
            IndispensableCommands.LOGGER.error("Failed to load language file: " + lang, e);
        }
    }

    public static String getTranslation(UUID playerId, String key) {
        String lang = getPlayerLanguage(playerId);
        Map<String, String> langMap = translations.get(lang);

        if (langMap != null && langMap.containsKey(key)) {
            return langMap.get(key);
        }

        langMap = translations.get("en_us");
        if (langMap != null && langMap.containsKey(key)) {
            return langMap.get(key);
        }

        return key;
    }

    public static void setPlayerLanguage(UUID playerId, String language) {
        if (translations.containsKey(language)) {
            LanguageConfig.setLanguage(playerId, language);
        }
    }

    public static String getPlayerLanguage(UUID playerId) {
        return LanguageConfig.getLanguageOrDefault(playerId, defaultLanguage);
    }

    public static void setDefaultLanguage(String language) {
        if (translations.containsKey(language)) {
            defaultLanguage = language;
        }
    }
}