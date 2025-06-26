package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Mail {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("mail")
                        .then(argument("joueur", EntityArgumentType.player())
                                .suggests((context, builder) -> {
                                    ServerPlayerEntity joueurActuel = context.getSource().getPlayer();
                                    String nomJoueurActuel = joueurActuel != null ? joueurActuel.getName().getString() : "";

                                    return EntityArgumentType.player()
                                            .listSuggestions(context, builder)
                                            .thenApply(suggestions -> {
                                                suggestions.getList().removeIf(suggestion ->
                                                        suggestion.getText().equals(nomJoueurActuel));
                                                return suggestions;
                                            });
                                })
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity destinataire = EntityArgumentType.getPlayer(context, "joueur");
                                            String message = StringArgumentType.getString(context, "message");
                                            ServerPlayerEntity expediteur = source.getPlayer();

                                            if (expediteur == null) {
                                                String error = LocalizationManager.getTranslation(null, "command.mail.error.player_only");
                                                source.sendError(Text.literal(error).formatted(Formatting.RED));
                                                return 0;
                                            }

                                            Reply.setDernierExpediteur(destinataire, expediteur);

                                            // Construction du message formaté
                                            Text messageFormate = Text.empty()
                                                    .append(Text.literal(LocalizationManager.getTranslation(destinataire.getUuid(), "command.mail.header"))
                                                            .formatted(Formatting.AQUA))
                                                    .append(Text.literal(expediteur.getName().getString())
                                                            .formatted(Formatting.GREEN, Formatting.BOLD))
                                                    .append("\n")
                                                    .append(Text.literal(message).formatted(Formatting.WHITE));

                                            // Envoi du message
                                            destinataire.sendMessage(messageFormate, false);
                                            destinataire.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.PLAYERS, 1.0f, 1.0f);

                                            // Confirmation à l'expéditeur
                                            String confirmation = LocalizationManager.getTranslation(expediteur.getUuid(), "command.mail.sent")
                                                    .replace("%player%", destinataire.getName().getString());
                                            source.sendFeedback(() -> Text.literal(confirmation).formatted(Formatting.YELLOW), false);

                                            return 1;
                                        })
                                )
                        ));
    }
}