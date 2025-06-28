package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;

public class Help {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        var helpCommand = dispatcher.register(
                literal("help")
                        .executes(context -> {
                            afficherAide(context.getSource(), false);
                            return 1;
                        })
                        .then(literal("op")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    afficherAide(context.getSource(), true);
                                    return 1;
                                })
                        ));

        dispatcher.register(literal("aide")
                .executes(context -> {
                    afficherAide(context.getSource(), false);
                    return 1;
                })
                .then(literal("op")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            afficherAide(context.getSource(), true);
                            return 1;
                        })));
    }

    private static void afficherAide(ServerCommandSource source, boolean isAdmin) {
        List<Text> messages = new ArrayList<>();
        UUID playerUuid = source.getPlayer() != null ? source.getPlayer().getUuid() : null;

        // En-tête
        messages.add(Text.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                .formatted(Formatting.DARK_GRAY, Formatting.BOLD));
        messages.add(Text.empty());

        messages.add(Text.literal(LocalizationManager.getTranslation(playerUuid, "command.help.section.teleportation"))
                .formatted(Formatting.YELLOW, Formatting.BOLD));
        messages.add(createHelpEntry(playerUuid, "/spawn", "command.help.spawn"));
        messages.add(createHelpEntry(playerUuid, "/back", "command.help.back"));
        messages.add(createHelpEntry(playerUuid, "/sethome <home>", "command.help.sethome"));
        messages.add(createHelpEntry(playerUuid, "/home <home>", "command.help.home"));
        messages.add(createHelpEntry(playerUuid, "/homelist", "command.help.homelist"));
        messages.add(createHelpEntry(playerUuid, "/delhome <home>", "command.help.delhome"));
        messages.add(createHelpEntry(playerUuid, "/tpa <player>", "command.help.tpa"));
        messages.add(createHelpEntry(playerUuid, "/tpyes - /tpaccept", "command.help.tpyes"));
        messages.add(createHelpEntry(playerUuid, "/tpno - /tpdeny", "command.help.tpno"));
        messages.add(Text.empty());

        messages.add(Text.literal(LocalizationManager.getTranslation(playerUuid, "command.help.section.communication"))
                .formatted(Formatting.YELLOW, Formatting.BOLD));
        messages.add(createHelpEntry(playerUuid, "/mail <player> <message>", "command.help.mail"));
        messages.add(createHelpEntry(playerUuid, "/r - /reply <message>", "command.help.reply"));
        messages.add(Text.empty());

        messages.add(Text.literal(LocalizationManager.getTranslation(playerUuid, "command.help.section.utilities"))
                .formatted(Formatting.YELLOW, Formatting.BOLD));
        messages.add(createHelpEntry(playerUuid, "/lang", "command.help.lang"));
        messages.add(createHelpEntry(playerUuid, "/trash", "command.help.trash"));
        messages.add(createHelpEntry(playerUuid, "/hat", "command.help.hat"));
        messages.add(createHelpEntry(playerUuid, "/suicide", "command.help.suicide"));
        messages.add(Text.empty());

        messages.add(Text.literal(LocalizationManager.getTranslation(playerUuid, "command.help.section.help"))
                .formatted(Formatting.YELLOW, Formatting.BOLD));
        messages.add(createHelpEntry(playerUuid, "/help - /aide", "command.help.help"));

        messages.add(Text.empty());
        messages.add(Text.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                .formatted(Formatting.DARK_GRAY, Formatting.BOLD));

        for (Text message : messages) {
            source.sendFeedback(() -> message, false);
        }
    }

    private static Text createHelpEntry(UUID playerUuid, String command, String translationKey) {
        return Text.empty()
                .append(Text.literal(command).formatted(Formatting.GREEN))
                .append(": ")
                .append(Text.literal(LocalizationManager.getTranslation(playerUuid, translationKey))
                        .formatted(Formatting.GRAY));
    }
}