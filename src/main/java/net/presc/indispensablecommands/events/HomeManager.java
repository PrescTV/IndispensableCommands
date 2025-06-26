package net.presc.indispensablecommands.events;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.presc.indispensablecommands.data.Home;
import net.presc.indispensablecommands.data.HomeStorage;

import java.util.*;

public class HomeManager {
    private static Map<UUID, Map<String, Home>> homesParJoueur = new HashMap<>();
    private static MinecraftServer server;

    public static void setServer(MinecraftServer server) {
        HomeManager.server = server;
        homesParJoueur = HomeStorage.loadHomes(server);
    }

    public static Home getHome(ServerPlayerEntity joueur, String nom) {
        Map<String, Home> homes = homesParJoueur.get(joueur.getUuid());
        if (homes == null) return null;
        return homes.get(nom);
    }

    public static boolean ajouterHome(ServerPlayerEntity joueur, String nom) {
        UUID uuid = joueur.getUuid();
        Map<String, Home> homes = homesParJoueur.computeIfAbsent(uuid, k -> new HashMap<>());

        if (homes.size() >= 2 && !homes.containsKey(nom)) {
            return false;
        }

        RegistryKey<World> dimension = joueur.getWorld().getRegistryKey();
        BlockPos pos = joueur.getBlockPos();
        Home home = new Home(
                dimension,
                pos,
                joueur.getYaw(),
                joueur.getPitch(),
                nom
        );

        homes.put(nom, home);
        saveData();
        return true;
    }

    public static boolean supprimerHome(ServerPlayerEntity joueur, String nom) {
        Map<String, Home> homes = homesParJoueur.get(joueur.getUuid());
        if (homes != null && homes.remove(nom) != null) {
            saveData();
            return true;
        }
        return false;
    }

    public static List<String> getNomsHomes(ServerPlayerEntity joueur) {
        Map<String, Home> homes = homesParJoueur.get(joueur.getUuid());
        if (homes == null) return Collections.emptyList();
        return new ArrayList<>(homes.keySet());
    }

    private static void saveData() {
        if (server != null) {
            HomeStorage.saveHomes(server, homesParJoueur);
        }
    }
}