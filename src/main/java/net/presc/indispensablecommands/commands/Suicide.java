package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import static net.minecraft.server.command.CommandManager.*;

public class Suicide {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("suicide")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();

                    if (player == null) {
                        String error = LocalizationManager.getTranslation(null, "command.suicide.error.player_only");
                        ctx.getSource().sendError(Text.literal(error));
                        return 0;
                    }

                    String message = LocalizationManager.getTranslation(player.getUuid(), "command.suicide.message");
                    player.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
                    player.kill(player.getServerWorld());
                    return 1;
                }));
    }
}