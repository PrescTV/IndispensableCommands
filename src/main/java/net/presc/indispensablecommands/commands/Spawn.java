package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.presc.indispensablecommands.events.LocalizationManager;

import java.util.EnumSet;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class Spawn {
    public static void enregistrer(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("spawn")
                .requires(source -> source.hasPermissionLevel(0))
                .executes(context -> {
                    ServerPlayerEntity joueur = context.getSource().getPlayer();
                    assert joueur != null;
                    ServerWorld overworld = Objects.requireNonNull(joueur.getServer()).getWorld(World.OVERWORLD);
                    assert overworld != null;
                    BlockPos spawn = overworld.getSpawnPos();

                    joueur.teleport(
                            overworld,
                            spawn.getX() + 0.5,
                            spawn.getY(),
                            spawn.getZ() + 0.5,
                            EnumSet.noneOf(PositionFlag.class),
                            joueur.getYaw(),
                            joueur.getPitch(),
                            false
                    );

                    String message = LocalizationManager.getTranslation(joueur.getUuid(), "command.spawn.success");
                    joueur.sendMessage(Text.literal(message).formatted(Formatting.YELLOW), false);

                    return 1;
                }));
    }
}