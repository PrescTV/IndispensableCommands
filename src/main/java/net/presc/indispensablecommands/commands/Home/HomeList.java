package net.presc.indispensablecommands.commands.Home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.HomeManager;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class HomeList {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("homelist")
                .executes(context -> {
                    ServerPlayerEntity joueur = context.getSource().getPlayer();
                    assert joueur != null;
                    List<String> nomsHomes = HomeManager.getNomsHomes(joueur);

                    if (nomsHomes.isEmpty()) {
                        String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.homelist.empty");
                        joueur.sendMessage(Text.literal(message).formatted(Formatting.GOLD), false);
                    } else {
                        String header = LocalizationManager.getTranslation(joueur.getUuid(), "command.homelist.header")
                                .replace("%count%", String.valueOf(nomsHomes.size()))
                                .replace("%max%", "2"); // Vous pourriez aussi mettre le max en configuration
                        joueur.sendMessage(Text.literal(header).formatted(Formatting.GOLD), false);

                        for (String nom : nomsHomes) {
                            String item = LocalizationManager.getTranslation(joueur.getUuid(), "command.homelist.item")
                                    .replace("%name%", nom);
                            joueur.sendMessage(Text.literal(item).formatted(Formatting.GREEN), false);
                        }
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}