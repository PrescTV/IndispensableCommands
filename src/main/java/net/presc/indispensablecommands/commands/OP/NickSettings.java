package net.presc.indispensablecommands.commands.OP;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.IndispensableCommands;
import net.presc.indispensablecommands.events.LocalizationManager;

public class NickSettings {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("nicklength")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("length", IntegerArgumentType.integer(1, 32))
                                .executes(ctx -> {
                                    int length = IntegerArgumentType.getInteger(ctx, "length");
                                    IndispensableCommands.getConfig().setMaxNameLength(length);
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    assert player != null;
                                    String message = String.format(
                                            LocalizationManager.getTranslation(player.getUuid(), "command.nicklength.set"),
                                            length
                                    );
                                    ctx.getSource().sendFeedback(() -> Text.literal(message).formatted(Formatting.GREEN), true);
                                    return 1;
                                })
                        )
        );

        dispatcher.register(
                CommandManager.literal("nickblacklist")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("surnom", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String pattern = StringArgumentType.getString(ctx, "surnom");
                                            IndispensableCommands.getConfig().addBlacklistedPattern(pattern);
                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                            assert player != null;
                                            String message = String.format(
                                                    LocalizationManager.getTranslation(player.getUuid(), "command.nickblacklist.add"),
                                                    pattern
                                            );
                                            ctx.getSource().sendFeedback(() -> Text.literal(message).formatted(Formatting.RED), true);
                                            return 1;
                                        })
                                )
                        )
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("surnom", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String pattern = StringArgumentType.getString(ctx, "surnom");
                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                            boolean removed = IndispensableCommands.getConfig().removeBlacklistedPattern(pattern);
                                            if (removed) {
                                                assert player != null;
                                                String message = String.format(
                                                        LocalizationManager.getTranslation(player.getUuid(), "command.nickblacklist.remove"),
                                                        pattern
                                                );
                                                ctx.getSource().sendFeedback(() -> Text.literal(message).formatted(Formatting.GREEN), true);
                                            } else {
                                                assert player != null;
                                                String error = String.format(
                                                        LocalizationManager.getTranslation(player.getUuid(), "command.nickblacklist.notfound"),
                                                        pattern
                                                );
                                                ctx.getSource().sendError(Text.literal(error));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(CommandManager.literal("clear")
                                .executes(ctx -> {
                                    IndispensableCommands.getConfig().clearBlacklist();
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    assert player != null;
                                    String message = LocalizationManager.getTranslation(player.getUuid(), "command.nickblacklist.clear");
                                    ctx.getSource().sendFeedback(() -> Text.literal(message).formatted(Formatting.YELLOW), true);
                                    return 1;
                                })
                        )
        );
    }
}
