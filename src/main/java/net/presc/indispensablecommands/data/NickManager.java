package net.presc.indispensablecommands.data;

import com.google.gson.JsonParser;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;
import net.presc.indispensablecommands.IndispensableCommands;
import net.presc.indispensablecommands.network.FakeTextDisplayHolder;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NickManager extends PersistentState {
    private static final Codec<Text> LEGACY_TEXT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Text, T>> decode(DynamicOps<T> ops, T input) {
            if (ops instanceof RegistryOps<?> registryOps) {
                return ops.getStringValue(input).map(string -> {
                            JsonReader reader = new JsonReader(new StringReader(string));
                            reader.setStrictness(Strictness.LENIENT);
                            return JsonParser.parseReader(reader);
                        }).flatMap(element -> TextCodecs.CODEC.parse(registryOps.withDelegate(JsonOps.INSTANCE), element))
                        .map(text -> Pair.of(text, ops.empty()));
            }
            return DataResult.error(() -> "Decoding text requires registry ops");
        }

        @Override
        public <T> DataResult<T> encode(Text input, DynamicOps<T> ops, T prefix) {
            return DataResult.error(() -> "Unsupported operation; legacy codec should not be used to encode");
        }
    };

    private static final Codec<Text> NAME_TEXT_CODEC = Codec.either(LEGACY_TEXT_CODEC, TextCodecs.CODEC)
            .xmap(Either::unwrap, Either::right);
    private static final Codec<Map<UUID, Text>> NAME_MAP_CODEC = Codec.unboundedMap(Uuids.STRING_CODEC, NAME_TEXT_CODEC);

    private final NickConfig config;
    private final Map<UUID, Text> playerNicknames = new HashMap<>();
    private final Map<UUID, Text> fullPlayerNames = new HashMap<>();

    private NickManager(MinecraftServer server, NickConfig config, Map<UUID, Text> nicknames) {
        this.config = config;
        this.playerNicknames.putAll(nicknames);
        IndispensableCommands.LOGGER.info("Creating player nick mappings");
    }

    public void updatePlayerName(ServerPlayerEntity player, Text name, NameType type) {
        if (name == null) {
            playerNicknames.remove(player.getUuid());
        } else {
            playerNicknames.put(player.getUuid(), name);
        }
        markDirty(player);
    }

    public Text getFullPlayerName(ServerPlayerEntity player) {
        if (!fullPlayerNames.containsKey(player.getUuid())) {
            updateFullPlayerName(player);
        }
        return fullPlayerNames.get(player.getUuid());
    }

    private void markDirty(ServerPlayerEntity player) {
        updateFullPlayerName(player);
        markDirty();
    }

    private void updateFullPlayerName(ServerPlayerEntity player) {
        Text nickname = playerNicknames.get(player.getUuid());
        MutableText name = Text.literal("");

        if (nickname != null) {
            name.append(nickname);
        } else {
            name.append(player.getName());
        }

        fullPlayerNames.put(player.getUuid(), name);
        ((FakeTextDisplayHolder) player).customName$updateName();
    }

    private static PersistentStateType<NickManager> type(MinecraftServer server, NickConfig config) {
        Codec<NickManager> codec = RecordCodecBuilder.create(instance ->
                instance.group(
                        NAME_MAP_CODEC.fieldOf("nicknames").forGetter(manager -> manager.playerNicknames)
                ).apply(instance, nicknames -> new NickManager(server, config, nicknames))
        );
        return new PersistentStateType<>(
                IndispensableCommands.MOD_ID + "_nicks",
                () -> new NickManager(server, config, Map.of()),
                codec,
                null
        );
    }

    public static NickManager getPlayerNameManager(MinecraftServer server, NickConfig config) {
        return server.getWorld(World.OVERWORLD).getPersistentStateManager()
                .getOrCreate(type(server, config));
    }

    public enum NameType {
        NICKNAME("nickname", "customname.nick", "Nickname");

        private final String name;
        private final String permission;
        private final String displayName;

        NameType(String name, String permission, String displayName) {
            this.name = name;
            this.permission = permission;
            this.displayName = displayName;
        }

        public String getName() {
            return name;
        }

        public String getPermission() {
            return permission;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}