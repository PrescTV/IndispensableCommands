package net.presc.indispensablecommands.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.presc.indispensablecommands.data.LastDeathPos;

public class DeathManager {
    public static void enregistrer() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayerEntity joueur) {
                LastDeathPos.enregistrer(
                        joueur.getUuid(),
                        joueur.getServerWorld(),
                        joueur.getBlockPos(),
                        joueur.getYaw(),
                        joueur.getPitch()
                );
            }
        });
    }
}
