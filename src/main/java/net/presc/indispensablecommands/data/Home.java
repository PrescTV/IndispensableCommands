package net.presc.indispensablecommands.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.presc.indispensablecommands.IndispensableCommands;

public class Home {
    @SerializedName("dimension")
    private final String dimensionId;
    private final BlockPos position;
    private final float yaw;
    private final float pitch;
    private final String nom;
    private transient RegistryKey<World> dimension;

    public Home(RegistryKey<World> dimension, BlockPos position, float yaw, float pitch, String nom) {
        this.dimension = dimension;
        this.dimensionId = dimension.getValue().toString();
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.nom = nom;
    }

    public void initDimension(MinecraftServer server) {
        if (this.dimension == null && this.dimensionId != null) {
            // Correction ici : on split le string pour avoir namespace et path
            Identifier id = Identifier.tryParse(this.dimensionId);
            if (id != null) {
                this.dimension = RegistryKey.of(RegistryKeys.WORLD, id);
            } else {
                IndispensableCommands.LOGGER.error("Invalid dimension ID format: " + this.dimensionId);
            }
        }
    }

    public RegistryKey<World> dimension() {
        return dimension;
    }

    public BlockPos position() {
        return position;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public String nom() {
        return nom;
    }
}