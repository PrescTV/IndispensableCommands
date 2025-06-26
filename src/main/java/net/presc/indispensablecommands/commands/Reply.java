package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Reply {
    private static final Map<UUID, UUID> derniersExpediteurs = new HashMap<>();

    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        var replyCommand = literal("reply")
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> executeReponse(context.getSource(), StringArgumentType.getString(context, "message"))));

        dispatcher.register(replyCommand);
        dispatcher.register(literal("r").redirect(dispatcher.register(replyCommand)));
    }

    public static void setDernierExpediteur(ServerPlayerEntity destinataire, ServerPlayerEntity expediteur) {
        derniersExpediteurs.put(destinataire.getUuid(), expediteur.getUuid());
    }

    private static int executeReponse(ServerCommandSource source, String message) {
        ServerPlayerEntity expediteur = source.getPlayer();
        if (expediteur == null) {
            String error = LocalizationManager.getTranslation(null, "command.reply.error.player_only");
            source.sendError(Text.literal(error).formatted(Formatting.RED));
            return 0;
        }

        UUID dernierExpediteurUUID = derniersExpediteurs.get(expediteur.getUuid());
        if (dernierExpediteurUUID == null) {
            String error = LocalizationManager.getTranslation(expediteur.getUuid(), "command.reply.error.no_message");
            source.sendError(Text.literal(error).formatted(Formatting.RED));
            return 0;
        }

        ServerPlayerEntity destinataire = source.getServer().getPlayerManager().getPlayer(dernierExpediteurUUID);
        if (destinataire == null) {
            String error = LocalizationManager.getTranslation(expediteur.getUuid(), "command.reply.error.offline");
            source.sendError(Text.literal(error).formatted(Formatting.RED));
            return 0;
        }

        // Construction du message formaté
        Text messageFormate = Text.empty()
                .append(Text.literal(LocalizationManager.getTranslation(destinataire.getUuid(), "command.reply.header"))
                        .formatted(Formatting.AQUA))
                .append(Text.literal(expediteur.getName().getString())
                        .formatted(Formatting.GREEN, Formatting.BOLD))
                .append("\n")
                .append(Text.literal(message).formatted(Formatting.WHITE));

        // Envoi du message
        destinataire.sendMessage(messageFormate, false);
        destinataire.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.PLAYERS, 1.0f, 1.0f);

        setDernierExpediteur(destinataire, expediteur);

        // Confirmation à l'expéditeur
        String confirmation = LocalizationManager.getTranslation(expediteur.getUuid(), "command.reply.sent")
                .replace("%player%", destinataire.getName().getString());
        source.sendFeedback(() -> Text.literal(confirmation).formatted(Formatting.YELLOW), false);

        return 1;
    }
}