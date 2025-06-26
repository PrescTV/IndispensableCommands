package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.data.LastDeathPos;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.EnumSet;

import static net.minecraft.server.command.CommandManager.literal;

public class Back {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("back")
                .requires(source -> source.hasPermissionLevel(0))
                .executes(context -> {
                    ServerPlayerEntity joueur = context.getSource().getPlayer();
                    assert joueur != null;
                    var info = LastDeathPos.obtenir(joueur.         getUuid());

                    if (info == null) {
                        String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.back.no_death");
                        joueur.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
                        return 0;
                    }

                    joueur.teleport(
                            info.monde,
                            info.position.getX() + 0.5,
                            info.position.getY(),
                            info.position.getZ() + 0.5,
                            EnumSet.noneOf(PositionFlag.class),
                            info.yaw,
                            info.pitch,
                            false
                    );

                    String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.back.success");
                    joueur.sendMessage(Text.literal(message).formatted(Formatting.YELLOW), false);
                    return 1;
                }));
    }
}
