package net.presc.indispensablecommands.commands.OP;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.IndispensableCommands;
import net.presc.indispensablecommands.data.NickManager;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.io.IOException;
import java.io.StringReader;

public class Nick {
    private static final char FORMATTING_CODE = '&';
    private static final char HEX_CODE = '#';

    public static void enregistrer(CommandManager.RegistrationEnvironment environment,
                                   com.mojang.brigadier.CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("nick")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests((context, builder) -> CommandSource.suggestMatching(
                                        context.getSource().getServer().getPlayerManager().getPlayerList().stream()
                                                .map(player -> player.getName().getString()),
                                        builder
                                ))

                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(updatePlayerNick())
                                )
                        )
        );
    }

    private static Command<ServerCommandSource> updatePlayerNick() {
        return context -> {
            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
            Text name;

            try {
                name = nickArgumentToText(StringArgumentType.getString(context, "name"), true);
            } catch (IllegalArgumentException exception) {
                throw new SimpleCommandExceptionType(Text.of(exception.getMessage())).create();
            }

            if (invalidNickArgument(name)) {
                throw new SimpleCommandExceptionType(Text.of(
                        LocalizationManager.getTranslation(context.getSource().getPlayer().getUuid(), "command.nick.invalid")
                )).create();
            }

            NickManager.getPlayerNameManager(context.getSource().getServer(), IndispensableCommands.getConfig())
                    .updatePlayerName(player, name, NickManager.NameType.NICKNAME);

            context.getSource().sendFeedback(
                    () -> Text.literal(
                            String.format(
                                    LocalizationManager.getTranslation(context.getSource().getPlayer().getUuid(), "command.nick.success"),
                                    player.getName().getString(), name.getString()
                            )
                    ).formatted(Formatting.GOLD),
                    true
            );
            updateListName(player);
            return 1;
        };
    }

    private static boolean invalidNickArgument(Text argument) {
        String name = Formatting.strip(argument.getString());
        return name == null || name.isEmpty()
                || IndispensableCommands.getConfig().nameBlacklisted(name)
                || name.length() > IndispensableCommands.getConfig().maxNameLength();
    }

    private static Text nickArgumentToText(String argument, boolean spaceAllowed) {
        return argumentToText(argument, IndispensableCommands.getConfig().formattingEnabled(), spaceAllowed, false);
    }

    private static Text argumentToText(String argument, boolean formattingEnabled,
                                       boolean spaceAllowed, boolean forceItalics) {
        if (!spaceAllowed) {
            argument = argument.split(" ")[0];
        }
        if (formattingEnabled) {
            MutableText complete = Text.empty();
            StringReader argumentReader = new StringReader(argument);
            StringBuilder currentText = new StringBuilder();
            Style currentStyle = Style.EMPTY;
            if (forceItalics) {
                currentStyle = currentStyle.withItalic(false);
            }

            try {
                int c = argumentReader.read();
                boolean formatting = false;
                boolean wasFormatting = false;
                while (c != -1) {
                    char current = (char) c;

                    if (current == FORMATTING_CODE) {
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
                        currentStyle = Style.EMPTY;
                        if (forceItalics) {
                            currentStyle = currentStyle.withItalic(false);
                        }

                        char[] hexChars = new char[6];
                        int read = argumentReader.read(hexChars, 0, 6);
                        if (read < 6) {
                            throw new IllegalArgumentException("Invalid hexadecimal color code");
                        }

                        try {
                            int colour = Integer.parseInt(String.valueOf(hexChars), 16);
                            currentStyle = currentStyle.withColor(colour);
                        } catch (NumberFormatException exception) {
                            throw new IllegalArgumentException("Invalid hexadecimal color code", exception);
                        }

                        wasFormatting = true;
                    } else if (formatting) {
                        formatting = false;

                        Formatting newStyle = Formatting.byCode(current);
                        if (newStyle == null) {
                            throw new IllegalArgumentException("Invalid formatting code");
                        }

                        if (newStyle.isColor() || newStyle == Formatting.RESET || !wasFormatting) {
                            if (!currentText.isEmpty()) {
                                complete.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                            }

                            currentText = new StringBuilder();
                            currentStyle = Style.EMPTY;
                            if (forceItalics) {
                                currentStyle = currentStyle.withItalic(false);
                            }
                        }
                        wasFormatting = true;
                        currentStyle = currentStyle.withFormatting(newStyle);
                    } else {
                        wasFormatting = false;
                        currentText.append(current);
                    }

                    c = argumentReader.read();
                }
            } catch (IOException exception) {
                throw new AssertionError(exception);
            }

            if (!currentText.isEmpty()) {
                complete.append(Text.literal(currentText.toString()).setStyle(currentStyle));
            }
            return complete;
        }
        return Text.of(argument);
    }

    public static void updateListName(ServerPlayerEntity player) {
        if (player.getServer() != null) {
            player.getServer().getPlayerManager()
                    .sendToAll(new PlayerListS2CPacket(Action.UPDATE_DISPLAY_NAME, player));
        }
    }
}
