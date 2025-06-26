package net.presc.indispensablecommands;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.presc.indispensablecommands.commands.*;
import net.presc.indispensablecommands.commands.Home.DelHome;
import net.presc.indispensablecommands.commands.Home.Home;
import net.presc.indispensablecommands.commands.Home.HomeList;
import net.presc.indispensablecommands.commands.Home.SetHome;
import net.presc.indispensablecommands.commands.OP.*;
import net.presc.indispensablecommands.commands.OP.Cooldown;
import net.presc.indispensablecommands.commands.TPA.Tpa;
import net.presc.indispensablecommands.commands.TPA.Tpno;
import net.presc.indispensablecommands.commands.TPA.Tpyes;
import net.presc.indispensablecommands.data.NickConfig;
import net.presc.indispensablecommands.events.DeathManager;
import net.presc.indispensablecommands.events.HomeManager;
import net.presc.indispensablecommands.events.LocalizationManager;
import net.presc.indispensablecommands.events.RespawnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class IndispensableCommands implements ModInitializer {
	public static final String MOD_ID = "IndispensableCommands";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static NickConfig config;

	private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("indispensable_commands");
	private static final Path NICK_CONFIG_PATH = CONFIG_DIR.resolve("nickconfig.json");

	public static Path getNickConfigPath() {
		return NICK_CONFIG_PATH;
	}

	public static Path getConfigDir() {
		return CONFIG_DIR;
	}

	@Override
	public void onInitialize() {
		config = NickConfig.readOrCreate();
		LocalizationManager.initialize();

		ServerLifecycleEvents.SERVER_STARTING.register(HomeManager::setServer);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			// OPs
			HelpOP.register(dispatcher);
			SetSpawn.enregistrer(dispatcher);
			Nick.enregistrer(environment, dispatcher);
			LangCommand.register(dispatcher);
			Freeze.register(dispatcher);
			Cooldown.register(dispatcher);
			NickSettings.register(dispatcher);
			Alert.register(dispatcher);

			// Homes
			SetHome.enregistrer(dispatcher);
			Home.enregistrer(dispatcher);
			HomeList.enregistrer(dispatcher);
			DelHome.enregistrer(dispatcher);

			// TPA
			Tpa.register(dispatcher);
			Tpyes.register(dispatcher);
			Tpno.register(dispatcher);

			// Misc
			Spawn.enregistrer(dispatcher);
			Help.enregistrer(dispatcher);
			Back.enregistrer(dispatcher);
			Trash.enregistrer(dispatcher);
			Hat.register(dispatcher);
			Suicide.register(dispatcher);
			Mail.enregistrer(dispatcher);
			Reply.enregistrer(dispatcher);

			// Manager
			RespawnManager.enregistrer();
			DeathManager.enregistrer();
		});
	}

	public static NickConfig getConfig() {
		return config;
	}
}