package net.presc.indispensablecommands.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class RespawnManager {
    public static void enregistrer() {
        ServerPlayerEvents.AFTER_RESPAWN.register((ancienJoueur, nouveauJoueur, perdu) -> {
            ServerWorld overworld = nouveauJoueur.getServer().getWorld(World.OVERWORLD);
            assert overworld != null;
            BlockPos spawn = overworld.getSpawnPos();

            nouveauJoueur.teleport(
                    overworld,
                    spawn.getX() + 0.5,
                    spawn.getY(),
                    spawn.getZ() + 0.5,
                    EnumSet.noneOf(PositionFlag.class),
                    nouveauJoueur.getYaw(),
                    nouveauJoueur.getPitch(),
                    false
            );
        });
    }
}