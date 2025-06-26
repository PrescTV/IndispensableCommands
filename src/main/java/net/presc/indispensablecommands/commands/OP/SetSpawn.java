package net.presc.indispensablecommands.commands.OP;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

public class SetSpawn {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("setspawn")
                        .requires(source -> source.hasPermissionLevel(2)) // Seulement pour les OPs
                        .executes(SetSpawn::executer)
        );
    }

    private static int executer(CommandContext<ServerCommandSource> contexte) {
        ServerPlayerEntity joueur = contexte.getSource().getPlayer();
        assert joueur != null;
        ServerWorld monde = joueur.getServerWorld();

        monde.setSpawnPos(joueur.getBlockPos(), 0.0f); // Définit la position du spawn

        String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.setspawn.success");

        contexte.getSource().sendFeedback(
                () -> Text.literal(message).formatted(Formatting.GOLD),
                true
        );

        return Command.SINGLE_SUCCESS;
    }
}
