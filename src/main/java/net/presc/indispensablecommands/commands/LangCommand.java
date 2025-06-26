package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.*;

public class LangCommand {
    private static final Map<String, String> LANGUAGE_MAPPINGS = new LinkedHashMap<String, String>() {{
        put("francais", "fr_fr");
        put("english", "en_us");
        put("deutsch", "de_de");
        put("espanol", "es_es");
        put("hindi", "hi_in");
        put("japanese", "ja_jp");
        put("chinese", "zh_cn");
    }};

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("lang")
                .then(argument("language", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String input = builder.getRemaining().toLowerCase();
                            for (String langName : LANGUAGE_MAPPINGS.keySet()) {
                                if (langName.toLowerCase().startsWith(input)) {
                                    builder.suggest(langName);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            String languageName = StringArgumentType.getString(context, "language").toLowerCase();

                            Optional<Map.Entry<String, String>> matchedLang = LANGUAGE_MAPPINGS.entrySet().stream()
                                    .filter(entry -> entry.getKey().toLowerCase().startsWith(languageName))
                                    .findFirst();

                            if (matchedLang.isPresent()) {
                                String languageCode = matchedLang.get().getValue();
                                assert player != null;
                                LocalizationManager.setPlayerLanguage(player.getUuid(), languageCode);

                                String message = String.format(
                                        LocalizationManager.getTranslation(player.getUuid(), "command.lang.success"),
                                        matchedLang.get().getKey() // Utilise le nom complet stocké
                                );
                                player.sendMessage(Text.literal(message).formatted(Formatting.GREEN), false);
                                return 1;
                            } else {
                                assert player != null;
                                String error = LocalizationManager.getTranslation(
                                        player.getUuid(), "command.lang.unsupported");
                                player.sendMessage(Text.literal(error).formatted(Formatting.RED), false);
                                return 0;
                            }
                        })
                ));
    }
}