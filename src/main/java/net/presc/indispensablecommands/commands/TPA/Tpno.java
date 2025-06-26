package net.presc.indispensablecommands.commands.TPA;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.TpaManager;
import net.presc.indispensablecommands.events.LocalizationManager;

import static net.minecraft.server.command.CommandManager.literal;

public class Tpno {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpno")
                .executes(ctx -> {
                    ServerPlayerEntity target = ctx.getSource().getPlayer();
                    if (target == null) return 0;

                    if (TpaManager.denyRequest(target)) {
                        String message = LocalizationManager.getTranslation(
                                target.getUuid(),
                                "command.tpa.no_request_to_deny"
                        );
                        target.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
                        return 0;
                    }

                    String successMessage = LocalizationManager.getTranslation(
                            target.getUuid(),
                            "command.tpa.request_denied"
                    );
                    target.sendMessage(Text.literal(successMessage).formatted(Formatting.GOLD), false);
                    return 1;
                }));

        dispatcher.register(literal("tpdeny")
                .executes(ctx -> {
                    ServerPlayerEntity target = ctx.getSource().getPlayer();
                    if (target == null) return 0;

                    if (TpaManager.denyRequest(target)) {
                        String message = LocalizationManager.getTranslation(
                                target.getUuid(),
                                "command.tpa.no_active_request"
                        );
                        target.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
                        return 0;
                    }

                    String successMessage = LocalizationManager.getTranslation(
                            target.getUuid(),
                            "command.tpa.request_cancelled"
                    );
                    target.sendMessage(Text.literal(successMessage).formatted(Formatting.YELLOW), false);
                    return 1;
                }));
    }
}