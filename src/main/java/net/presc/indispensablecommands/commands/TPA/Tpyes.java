package net.presc.indispensablecommands.commands.TPA;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.TpaManager;
import net.presc.indispensablecommands.events.LocalizationManager;

import static net.minecraft.server.command.CommandManager.literal;

public class Tpyes {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpyes")
                .executes(ctx -> {
                    ServerPlayerEntity target = ctx.getSource().getPlayer();

                    if (!TpaManager.hasPendingRequest(target)) {
                        String message = LocalizationManager.getTranslation(target.getUuid(), "command.tpa.no_pending_request");
                        target.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
                        return 0;
                    }

                    ServerPlayerEntity sender = TpaManager.acceptRequest(target);
                    if (sender != null) {
                        sender.requestTeleport(target.getX(), target.getY(), target.getZ());
                        sender.setYaw(target.getYaw());
                        sender.setPitch(target.getPitch());

                        String senderMsg = LocalizationManager.getTranslation(sender.getUuid(), "command.tpa.sender_teleported")
                                .replace("%target%", target.getName().getString());
                        sender.sendMessage(Text.literal(senderMsg).formatted(Formatting.GREEN), false);

                        String targetMsg = LocalizationManager.getTranslation(target.getUuid(), "command.tpa.target_teleported")
                                .replace("%sender%", sender.getName().getString());
                        target.sendMessage(Text.literal(targetMsg).formatted(Formatting.GREEN), false);
                    }
                    return 1;
                }));

        dispatcher.register(literal("tpaccept")
                .executes(ctx -> {
                    ServerPlayerEntity target = ctx.getSource().getPlayer();

                    if (!TpaManager.hasPendingRequest(target)) {
                        String message = LocalizationManager.getTranslation(target.getUuid(), "command.tpa.no_request_to_accept");
                        target.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
                        return 0;
                    }

                    ServerPlayerEntity sender = TpaManager.acceptRequest(target);
                    if (sender != null) {
                        sender.requestTeleport(target.getX(), target.getY(), target.getZ());
                        sender.setYaw(target.getYaw());
                        sender.setPitch(target.getPitch());

                        String senderMsg = LocalizationManager.getTranslation(sender.getUuid(), "command.tpa.sender_accepted")
                                .replace("%target%", target.getName().getString());
                        sender.sendMessage(Text.literal(senderMsg).formatted(Formatting.GREEN), false);

                        String targetMsg = LocalizationManager.getTranslation(target.getUuid(), "command.tpa.target_accepted")
                                .replace("%sender%", sender.getName().getString());
                        target.sendMessage(Text.literal(targetMsg).formatted(Formatting.GREEN), false);
                    }
                    return 1;
                }));
    }
}