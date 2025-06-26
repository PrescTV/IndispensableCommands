package net.presc.indispensablecommands.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TpaManager {
    private static final Map<UUID, UUID> pendingRequests = new HashMap<>();

    private static final Map<UUID, Long> cooldowns = new HashMap<>();


    private static long cooldownDuration = 30_000; // 30s default

    public static void addRequest(ServerPlayerEntity sender, ServerPlayerEntity target) {
        pendingRequests.put(target.getUuid(), sender.getUuid());
        cooldowns.put(sender.getUuid(), System.currentTimeMillis() + cooldownDuration);

        sender.sendMessage(
                Text.literal("Demande envoyée à " + target.getName().getString())
                        .formatted(Formatting.GREEN),
                false
        );
    }

    public static boolean hasPendingRequest(ServerPlayerEntity target) {
        return pendingRequests.containsKey(target.getUuid());
    }

    public static ServerPlayerEntity acceptRequest(ServerPlayerEntity target) {
        UUID senderUuid = pendingRequests.remove(target.getUuid());
        return Objects.requireNonNull(target.getServer()).getPlayerManager().getPlayer(senderUuid);
    }

    public static boolean denyRequest(ServerPlayerEntity target) {
        UUID senderUuid = pendingRequests.remove(target.getUuid());
        if (senderUuid != null) {
            ServerPlayerEntity sender = Objects.requireNonNull(target.getServer()).getPlayerManager().getPlayer(senderUuid);
            if (sender != null) {
                sender.sendMessage(
                        Text.literal(target.getName().getString() + " a refusé votre demande")
                                .formatted(Formatting.RED),
                        false
                );
            }
            return false;
        }
        return true;
    }

    public static boolean isOnCooldown(ServerPlayerEntity player) {
        return cooldowns.getOrDefault(player.getUuid(), 0L) > System.currentTimeMillis();
    }

    public static void clearRequests(ServerPlayerEntity player) {
        pendingRequests.values().remove(player.getUuid());
        pendingRequests.remove(player.getUuid());
        cooldowns.remove(player.getUuid());
    }

    public static void setCooldownDuration(long seconds) {
        cooldownDuration = seconds * 1000;
    }

    public static long getCooldownDuration() {
        return cooldownDuration / 1000;
    }

    public static long getRemainingCooldown(ServerPlayerEntity player) {
        long remaining = cooldowns.getOrDefault(player.getUuid(), 0L) - System.currentTimeMillis();
        return (remaining > 0) ? (remaining / 1000) + 1 : 0;
    }
}