package net.presc.indispensablecommands.commands.OP;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.io.IOException;
import java.io.StringReader;

import static net.minecraft.server.command.CommandManager.*;

public class Alert {

    private static final char FORMAT_CODE = '&';
    private static final char HEX_CODE = '#';

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("alert")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String rawMessage = StringArgumentType.getString(ctx, "message");
                            Text message;
                            try {
                                message = parseFormattedText(rawMessage);
                            } catch (IllegalArgumentException e) {
                                throw new SimpleCommandExceptionType(Text.of(e.getMessage())).create();
                            }

                            for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                player.sendMessage(message, false);
                            }

                            return 1;
                        })));

        dispatcher.register(literal("broadcast")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String rawMessage = StringArgumentType.getString(ctx, "message");
                            Text message;
                            try {
                                message = parseFormattedText(rawMessage);
                            } catch (IllegalArgumentException e) {
                                throw new SimpleCommandExceptionType(Text.of(e.getMessage())).create();
                            }

                            for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                player.sendMessage(message, false);
                            }

                            return 1;
                        })));
    }

    public static Text parseFormattedText(String input) {
        MutableText complete = Text.empty();
        StringReader reader = new StringReader(input.replace(FORMAT_CODE, '§'));
        StringBuilder currentText = new StringBuilder();
        Style currentStyle = Style.EMPTY.withBold(true);

        try {
            int c = reader.read();
            boolean formatting = false;
            boolean wasFormatting = false;

            while (c != -1) {
                char current = (char) c;

                if (current == '§') {
                    if (formatting) {
                        formatting = false;
                        wasFormatting = false;
                        currentText.append(current);
                    } else {
                        formatting = true;
                    }
                } else if (current == HEX_CODE && formatting) {
                    formatting = false;
                    if (!currentText.isEmpty()) {
                        complete.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                    }

                    currentText = new StringBuilder();
                    currentStyle = Style.EMPTY.withBold(true);

                    char[] hexChars = new char[6];
                    int read = reader.read(hexChars, 0, 6);
                    if (read < 6) {
                        String hexError = LocalizationManager.getTranslation(null, "command.alert.error.invalid_hex");
                        throw new IllegalArgumentException(hexError);
                    }

                    try {
                        int color = Integer.parseInt(String.valueOf(hexChars), 16);
                        currentStyle = currentStyle.withColor(TextColor.fromRgb(color));
                    } catch (NumberFormatException e) {
                        String hexError = LocalizationManager.getTranslation(null, "command.alert.error.invalid_hex");
                        throw new IllegalArgumentException(hexError);
                    }

                    wasFormatting = true;
                } else if (formatting) {
                    formatting = false;

                    Formatting newStyle = Formatting.byCode(current);
                    if (newStyle == null) {
                        String formatError = LocalizationManager.getTranslation(null, "command.alert.error.invalid_format")
                                .replace("%code%", String.valueOf(current));
                        throw new IllegalArgumentException(formatError);
                    }

                    if (newStyle.isColor() || newStyle == Formatting.RESET || !wasFormatting) {
                        if (!currentText.isEmpty()) {
                            complete.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                        }

                        currentText = new StringBuilder();
                        currentStyle = Style.EMPTY.withBold(true);
                    }

                    wasFormatting = true;
                    currentStyle = currentStyle.withFormatting(newStyle);
                } else {
                    wasFormatting = false;
                    currentText.append(current);
                }

                c = reader.read();
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        if (!currentText.isEmpty()) {
            complete.append(Text.literal(currentText.toString()).setStyle(currentStyle));
        }

        return complete;
    }
}
