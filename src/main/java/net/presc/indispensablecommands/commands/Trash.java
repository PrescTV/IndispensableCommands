package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import static net.minecraft.server.command.CommandManager.literal;
import net.presc.indispensablecommands.ui.TrashUI;

public class Trash {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        var trashCommand = literal("trash")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        player.openHandledScreen(new TrashUI.Factory());
                    }
                    return 1;
                });
        dispatcher.register(trashCommand);
    }
}