package net.presc.indispensablecommands.commands.OP;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.*;

public class Freeze {
    private static final Set<UUID> frozenPlayers = new HashSet<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("freeze")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("joueur", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            context.getSource().getServer().getPlayerManager()
                                    .getPlayerList()
                                    .forEach(p -> builder.suggest(p.getName().getString()));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            String playerName = StringArgumentType.getString(context, "joueur");
                            ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);

                            if (target == null) {
                                String msg = LocalizationManager.getTranslation(null, "command.freeze.error.not_found");
                                source.sendError(Text.literal(msg).formatted(Formatting.RED));
                                return 0;
                            }

                            boolean isFrozen = frozenPlayers.contains(target.getUuid());
                            if (isFrozen) {
                                frozenPlayers.remove(target.getUuid());
                            } else {
                                frozenPlayers.add(target.getUuid());
                            }

                            String adminMessageKey = isFrozen ? "command.freeze.admin.unfrozen" : "command.freeze.admin.frozen";
                            String adminMessage = LocalizationManager.getTranslation(null, adminMessageKey)
                                    .replace("%player%", target.getName().getString());
                            source.sendFeedback(() -> Text.literal(adminMessage).formatted(isFrozen ? Formatting.GREEN : Formatting.RED), true);

                            String targetMessageKey = isFrozen ? "command.freeze.target.unfrozen" : "command.freeze.target.frozen";
                            String targetMessage = LocalizationManager.getTranslation(target.getUuid(), targetMessageKey);
                            target.sendMessage(Text.literal(targetMessage).formatted(isFrozen ? Formatting.GREEN : Formatting.RED), false);

                            return 1;
                        })
                )
        );
    }

    public static boolean isFrozen(UUID playerId) {
        return frozenPlayers.contains(playerId);
    }
}
