package net.presc.indispensablecommands.commands.Home;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.presc.indispensablecommands.events.HomeManager;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Home {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("home")
                .then(argument("nom", greedyString())
                        .suggests(SUGGESTION_HOMES)
                        .executes(context -> {
                            ServerPlayerEntity joueur = context.getSource().getPlayer();
                            String nom = StringArgumentType.getString(context, "nom");

                            assert joueur != null;
                            net.presc.indispensablecommands.data.Home home = HomeManager.getHome(joueur, nom);
                            if (home == null) {
                                String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.home.error.not_found");
                                joueur.sendMessage(Text.literal(message.replace("%s", nom)).formatted(Formatting.RED), false);
                                return 0;
                            }

                            if (home.dimension() == null) {
                                String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.home.error.invalid_dimension");
                                joueur.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
                                return 0;
                            }

                            ServerWorld monde = Objects.requireNonNull(joueur.getServer()).getWorld(home.dimension());
                            if (monde == null) {
                                String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.home.error.dimension_unavailable");
                                joueur.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
                                return 0;
                            }

                            BlockPos pos = home.position();

                            joueur.teleport(
                                    monde,
                                    pos.getX() + 0.5,
                                    pos.getY(),
                                    pos.getZ() + 0.5,
                                    Collections.emptySet(),
                                    home.yaw(),
                                    home.pitch(),
                                    true
                            );

                            String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.home.success");
                            joueur.sendMessage(Text.literal(message.replace("%s", nom)).formatted(Formatting.GREEN), false);
                            return 1;
                        })
                )
        );
    }

    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_HOMES = (context, builder) -> {
        ServerPlayerEntity joueur = context.getSource().getPlayer();
        assert joueur != null;
        return suggest(HomeManager.getNomsHomes(joueur), builder);
    };

    private static CompletableFuture<Suggestions> suggest(Iterable<String> options, SuggestionsBuilder builder) {
        for (String option : options) {
            if (option.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(option);
            }
        }
        return builder.buildFuture();
    }
}