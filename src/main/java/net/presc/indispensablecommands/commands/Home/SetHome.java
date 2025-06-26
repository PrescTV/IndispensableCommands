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

public class SetHome {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("sethome")
                .then(argument("nom", greedyString())
                        .executes(context -> {
                            ServerPlayerEntity joueur = context.getSource().getPlayer();
                            String nom = StringArgumentType.getString(context, "nom");

                            assert joueur != null;
                            if (!HomeManager.ajouterHome(joueur, nom)) {
                                String erreur = LocalizationManager.getTranslation(joueur.getUuid(), "command.sethome.error.limit_or_exists");
                                joueur.sendMessage(Text.literal(erreur).formatted(net.minecraft.util.Formatting.RED), false);
                                return 0;
                            }

                            String succes = LocalizationManager.getTranslation(joueur.getUuid(), "command.sethome.success")
                                    .replace("%name%", nom);
                            joueur.sendMessage(Text.literal(succes).formatted(net.minecraft.util.Formatting.GREEN), false);
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}
