package net.presc.indispensablecommands.data;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.UUID;

public class LastDeathPos {
    public static class PositionMort {
        public final ServerWorld monde;
        public final BlockPos position;
        public final float yaw;
        public final float pitch;

        public PositionMort(ServerWorld monde, BlockPos position, float yaw, float pitch) {
            this.monde = monde;
            this.position = position;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    private static final HashMap<UUID, PositionMort> positions = new HashMap<>();

    public static void enregistrer(UUID joueurId, ServerWorld monde, BlockPos position, float yaw, float pitch) {
        positions.put(joueurId, new PositionMort(monde, position, yaw, pitch));
    }

    public static PositionMort obtenir(UUID joueurId) {
        return positions.get(joueurId);
    }

    public static boolean existe(UUID joueurId) {
        return positions.containsKey(joueurId);
    }
}
