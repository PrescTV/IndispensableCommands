package net.presc.indispensablecommands.commands.OP;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.TpaManager;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Cooldown {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpacooldown")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("secondes", IntegerArgumentType.integer(0, 3600)) //0s to 1h
                        .executes(ctx -> {
                            int seconds = IntegerArgumentType.getInteger(ctx, "secondes");
                            TpaManager.setCooldownDuration(seconds);
                            String message = String.format(
                                    LocalizationManager.getTranslation(Objects.requireNonNull(ctx.getSource().getPlayer()).getUuid(), "command.tpacooldown.set"),
                                    seconds
                            );
                            ctx.getSource().sendMessage(Text.literal(message).formatted(Formatting.GREEN));
                            return 1;
                        })));
    }
}
