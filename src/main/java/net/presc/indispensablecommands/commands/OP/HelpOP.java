package net.presc.indispensablecommands.commands.OP;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class HelpOP {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var command = literal("helpop")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    afficherAideAdmin(context.getSource());
                    return 1;
                });

        var alias = literal("aideop")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    afficherAideAdmin(context.getSource());
                    return 1;
                });

        dispatcher.register(command);
        dispatcher.register(alias);
    }

    private static void afficherAideAdmin(ServerCommandSource source) {
        List<Text> messages = new ArrayList<>();

        messages.add(Text.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").formatted(Formatting.DARK_GRAY, Formatting.BOLD));
        messages.add(Text.empty());

        messages.add(Text.literal(LocalizationManager.getTranslation(null, "command.helpop.title")).formatted(Formatting.RED, Formatting.BOLD));
        messages.add(createHelpEntry("/setspawn", LocalizationManager.getTranslation(null, "command.helpop.setspawn")));
        messages.add(createHelpEntry("/tpacooldown", LocalizationManager.getTranslation(null, "command.helpop.tpacooldown")));
        messages.add(createHelpEntry("/nick <pseudo>", LocalizationManager.getTranslation(null, "command.helpop.nick")));
        messages.add(createHelpEntry("/nicklength <1 - 64>", LocalizationManager.getTranslation(null, "command.helpop.nicklength")));
        messages.add(createHelpEntry("/nickblacklist <argument>", LocalizationManager.getTranslation(null, "command.helpop.nickblacklist")));
        messages.add(createHelpEntry("/freeze <player>", LocalizationManager.getTranslation(null, "command.helpop.freeze")));
        messages.add(createHelpEntry("/helpop - /aideop", LocalizationManager.getTranslation(null, "command.helpop.helpop")));
        messages.add(Text.empty());

        messages.add(Text.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").formatted(Formatting.DARK_GRAY, Formatting.BOLD));

        for (Text message : messages) {
            source.sendFeedback(() -> message, false);
        }
    }

    private static Text createHelpEntry(String command, String description) {
        return Text.empty()
                .append(Text.literal(command).formatted(Formatting.RED))
                .append(": ")
                .append(Text.literal(description).formatted(Formatting.GRAY));
    }
}
