package net.presc.indispensablecommands.commands.Home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.presc.indispensablecommands.events.HomeManager;
import net.presc.indispensablecommands.events.LocalizationManager;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DelHome {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("delhome")
                .then(argument("nom", greedyString())
                        .suggests(Home.SUGGESTION_HOMES)
                        .executes(context -> {
                            ServerPlayerEntity joueur = context.getSource().getPlayer();
                            String nom = StringArgumentType.getString(context, "nom");

                            assert joueur != null;
                            if (HomeManager.supprimerHome(joueur, nom)) {
                                String baseMessage = LocalizationManager.getTranslation(
                                        joueur.getUuid(),
                                        "command.delhome.success"
                                );
                                String message = baseMessage.replace("%s", nom);
                                joueur.sendMessage(Text.literal(message), false);
                                return Command.SINGLE_SUCCESS;
                            } else {
                                String baseMessage = LocalizationManager.getTranslation(
                                        joueur.getUuid(),
                                        "command.delhome.error.not_found"
                                );
                                String message = baseMessage.replace("%s", nom);
                                joueur.sendMessage(Text.literal(message), false);
                                return 0;
                            }
                        })
                ));
    }
}