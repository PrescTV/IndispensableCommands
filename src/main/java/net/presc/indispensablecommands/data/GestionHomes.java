package net.presc.indispensablecommands.data;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GestionHomes {
    public record Home(ServerWorld monde, BlockPos position, float yaw, float pitch) {}

    private static final Map<UUID, Map<String, Home>> homesParJoueur = new HashMap<>();

    public static boolean ajouterHome(UUID joueur, String nom, Home home) {
        Map<String, Home> homes = homesParJoueur.computeIfAbsent(joueur, k -> new HashMap<>());
        if (homes.size() >= 2 && !homes.containsKey(nom)) {
            return false; // Limite atteinte
        }
        homes.put(nom, home);
        return true;
    }

    public static Home getHome(UUID joueur, String nom) {
        return homesParJoueur.getOrDefault(joueur, new HashMap<>()).get(nom);
    }

    public static boolean supprimerHome(UUID joueur, String nom) {
        Map<String, Home> homes = homesParJoueur.get(joueur);
        if (homes != null) {
            return homes.remove(nom) != null;
        }
        return false;
    }

    public static Map<String, Home> getTousLesHomes(UUID joueur) {
        return homesParJoueur.getOrDefault(joueur, new HashMap<>());
    }
}
