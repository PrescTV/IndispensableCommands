package net.presc.indispensablecommands.commands.TPA;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.TpaManager;
import net.presc.indispensablecommands.events.LocalizationManager;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Tpa {
    private static final SuggestionProvider<ServerCommandSource> OTHER_PLAYERS_SUGGESTION = (context, builder) -> {
        ServerPlayerEntity self;
        try {
            self = context.getSource().getPlayer();
        } catch (Exception e) {
            return builder.buildFuture();
        }

        return CommandSource.suggestMatching(
                context.getSource().getServer().getPlayerManager().getPlayerList().stream()
                        .filter(player -> {
                            assert self != null;
                            return !player.getUuid().equals(self.getUuid());
                        })
                        .map(player -> player.getName().getString()),
                builder
        );
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpa")
                .then(argument("target", EntityArgumentType.player())
                        .suggests(OTHER_PLAYERS_SUGGESTION)
                        .executes(ctx -> {
                            ServerPlayerEntity sender = ctx.getSource().getPlayer();
                            ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");

                            assert sender != null;
                            if (sender.getUuid().equals(target.getUuid())) {
                                sender.sendMessage(
                                        Text.literal(LocalizationManager.getTranslation(sender.getUuid(), "command.tpa.self"))
                                                .formatted(Formatting.RED),
                                        false
                                );
                                return 0;
                            }

                            if (TpaManager.isOnCooldown(sender)) {
                                long remaining = TpaManager.getRemainingCooldown(sender);
                                sender.sendMessage(
                                        Text.literal(String.format(LocalizationManager.getTranslation(sender.getUuid(), "command.tpa.cooldown"), remaining))
                                                .formatted(Formatting.RED),
                                        false
                                );
                                return 0;
                            }

                            TpaManager.addRequest(sender, target);

                            target.sendMessage(
                                    Text.literal(String.format(LocalizationManager.getTranslation(target.getUuid(), "command.tpa.request"), sender.getName().getString()))
                                            .formatted(Formatting.GOLD)
                                            .append(Text.literal(" (/tpyes ou /tpno)").formatted(Formatting.YELLOW)),
                                    false
                            );

                            return 1;
                        })));
    }
}
