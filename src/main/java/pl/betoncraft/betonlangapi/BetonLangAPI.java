/**
 * BetonLangAPI - translations API for Bukkit plugins
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonlangapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonlangapi.database.Database;
import pl.betoncraft.betonlangapi.database.DatabaseException;
import pl.betoncraft.betonlangapi.database.FlatFile;
import pl.betoncraft.betonlangapi.database.MySQL;
import pl.betoncraft.betonlangapi.placeholderapi.PlaceholderHandler;

/**
 * <h1>BetonLangAPI</h1>
 * 
 * <p>
 * BetonLangAPI allows for unified translation of other plugins. It keeps track
 * of the language chosen by players and retrieves appropriate messages for
 * them.
 * </p>
 * 
 * <p>
 * If you want to register your plugin with this API you need to prepare the
 * translation properly. It can be either contained in one YAML file, with every
 * language as a separate section, or each language in it's own file, with the
 * language name appended to the file name after "_" character.
 * </p>
 * 
 * <p>
 * If you want your messages to have variables, you need to specify them as {0},
 * {1}, {2} etc, corresponding to the index of an array you're passing to the
 * {@link #getMessage(CommandSender, Plugin, String)} method.
 * </p>
 * 
 * <p>
 * In order to register your plugin in the API use {@link #registerPlugin(Plugin,
 * TranslatedPlugin)} method. Refer to the {@link TranslatedPlugin} javadocs to
 * learn how to instantiate it.
 * </p>
 * 
 * <p>
 * When your plugin is reloading, you should call {@link #reloadMessages(Plugin)}
 * method. It will load all languages specified at startup again.
 * </p>
 * 
 * <p>
 * Every time you want to send a player some message, use
 * {@link #getMessage(CommandSender, Plugin, String)} method. It will return
 * translated message with converted color codes and inserted variables.
 * </p>
 * 
 * @author Jakub Sapalski
 */
public class BetonLangAPI extends JavaPlugin implements Listener {

	private static BetonLangAPI instance;
	private static HashMap<String, TranslatedPlugin> plugins = new HashMap<>();
	private static HashMap<UUID, String> players = new HashMap<>();
	private static ArrayList<String> languages = new ArrayList<>();
	private static String globalDefaultLanguage;
	private static Database database;
	private static PlaceholderHandler placeholderHandler;

	@Override
	public void onEnable() {
		super.onEnable();
		// load initial data
		instance = this;
		saveDefaultConfig();
		globalDefaultLanguage = getConfig().getString("default_global_language", "en");
		// choose the database to use
		try {
			database = new MySQL(getConfig().getString("mysql.host"), getConfig().getString("mysql.port"),
					getConfig().getString("mysql.base"), getConfig().getString("mysql.user"),
					getConfig().getString("mysql.pass"));
			getLogger().info("Using MySQL for data storage");
		} catch (DatabaseException e) {
			try {
				database = new FlatFile();
			} catch (DatabaseException e1) {
				getLogger().severe("Could not find database, disabling.");
				setEnabled(false);
				return;
			}
			getLogger().info("Using YAML file for data storage");
		}
		// deploy default messages file
		File messages = new File(getDataFolder(), "messages.yml");
		if (!messages.exists()) {
			try {
				messages.createNewFile();
				InputStream in = getResource("messages.yml");
				if (in == null)
					return;
				OutputStream out = new FileOutputStream(messages);
				byte[] buffer = new byte[1024];
				int len = in.read(buffer);
				while (len != -1) {
					out.write(buffer, 0, len);
					len = in.read(buffer);
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// register important stuff
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("language").setExecutor(new Commands());
		getCommand("betonlangapi").setExecutor(new Commands());
		// add integration with PlaceholderAPI
		placeholderHandler = new PlaceholderHandler();
		// register this plugin, because it can interact with players via
		// /language command
		registerPlugin(this, new TranslatedPlugin(this, globalDefaultLanguage));
		// load all online players
		for (Player player : Bukkit.getOnlinePlayers()) {
			loadPlayer(player.getUniqueId());
		}
	}

	@Override
	public void onDisable() {
		// close the database connections
		database.finish();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		// don't load anything if the player was not let in
		if (event.getLoginResult() != Result.ALLOWED)
			return;
		loadPlayer(event.getUniqueId());
	}

	/**
	 * Loads player's language from the database and stores in in the plugin.
	 * 
	 * @param uuid
	 *            unique ID of the player
	 * @return the player's language
	 */
	private String loadPlayer(UUID uuid) {
		// load player's language or use default one
		String lang = BetonLangAPI.getDB().load(uuid);
		players.put(uuid, lang);
		return lang;
	}

	/**
	 * Returns the instance of the BetonLangAPI plugin. No need to use it, check
	 * other methods.
	 * 
	 * @return the BetonLangAPI instance
	 */
	public static BetonLangAPI getInstance() {
		return instance;
	}

	/**
	 * Returns the Database instance which is currently used.
	 * 
	 * @return Database instance used by the plugin
	 */
	public static Database getDB() {
		return database;
	}

	/**
	 * Returns the list of available languages.
	 * 
	 * @return the ArrayList of languages
	 */
	public static ArrayList<String> getLanguages() {
		return languages;
	}

	/**
	 * This method registers your plugin in the language API.
	 * 
	 * @param plugin
	 *            the plugin which owns the messages
	 * @param translatedPlugin
	 *            TranslatedPlugin object which represents your plugin; you need
	 *            to create new instance of this class for yourself
	 */
	public static void registerPlugin(Plugin plugin, TranslatedPlugin translatedPlugin) {
		plugins.put(plugin.getName(), translatedPlugin);
		reloadAvailableLanguages();
		instance.getLogger().info("Registered plugin '" + plugin.getName() + "'.");
	}

	/**
	 * Reloads the configuration file and all messages registered in the plugin.
	 */
	public static void reloadPlugin() {
		instance.reloadConfig();
		globalDefaultLanguage = instance.getConfig().getString("default_global_language", "en");
		for (TranslatedPlugin translatedPlugin : plugins.values()) {
			translatedPlugin.load();
		}
		reloadAvailableLanguages();
	}

	/**
	 * Call this method when you're reloading your plugin. This will reload all
	 * messages using your TranslationLoader instance.
	 * 
	 * @param plugin
	 *            the plugin you want to reload
	 */
	public static void reloadMessages(Plugin plugin) {
		TranslatedPlugin translatedPlugin = plugins.get(plugin.getName());
		if (translatedPlugin != null)
			translatedPlugin.load();
		reloadAvailableLanguages();
	}

	/**
	 * Sets the player's language to the specified one.
	 * 
	 * @param player
	 *            the Player
	 * @param languageCode
	 *            the language code
	 */
	public static void setLanguage(Player player, String languageCode) {
		final String lang = (languageCode == null) ? globalDefaultLanguage : languageCode;
		PlayerLanguageChangeEvent event = new PlayerLanguageChangeEvent(player, lang);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		players.put(player.getUniqueId(), lang);
		new BukkitRunnable() {
			@Override
			public void run() {
				database.update(player.getUniqueId(), lang);
			}
		}.runTaskAsynchronously(instance);
	}

	/**
	 * Returns the current language set for this player;
	 * 
	 * @param player
	 *            the Player
	 * @return the language code
	 */
	public static String getLanguage(Player player) {
		return players.get(player.getUniqueId());
	}

	/**
	 * Retrieves the message from the specified plugin in player's language. You
	 * should probably create a wrapper around this method to replace variables
	 * and color codes.
	 * 
	 * @param sender
	 *            command sender for whom this message should be retrieved; if
	 *            the player is null, returns the message in default language
	 * @param plugin
	 *            plugin owning this message
	 * @param messageName
	 *            name of the key used to store the message in YAML file
	 * @return the translated message
	 */
	public static String getMessage(CommandSender sender, Plugin plugin, String messageName) {
		String lang;
		if (sender == null || !(sender instanceof Player)) {
			lang = null;
		} else {
			lang = players.get(((Player) sender).getUniqueId());
		};
		String message = getMessage(lang, plugin, messageName);
		if (sender instanceof Player) {
			Player player = (Player) sender;
			message = placeholderHandler.resolve(player, message);
		}
		return message;
	}
	
	/**
	 * Retrieves the message from the specified plugin in specified language.
	 * 
	 * @param language
	 *            language of the message
	 * @param plugin
	 *            plugin containing the message
	 * @param messageName
	 *            name of the message
	 * @return the translated message
	 */
	public static String getMessage(String language, Plugin plugin, String messageName) {
		TranslatedPlugin translatedPlugin = plugins.get(plugin.getName());
		if (translatedPlugin == null)
			return null;
		if (language == null)
			language = translatedPlugin.getDefaultLang();
		return translatedPlugin.getMessage(language, messageName);
	}
	
	/**
	 * Returns the instance of the PlaceholderHandler.
	 * 
	 * @return the PlaceholderHandler instance
	 */
	public static PlaceholderHandler getPlaceholders() {
		return placeholderHandler;
	}

	/**
	 * Reloads available languages.
	 */
	private static void reloadAvailableLanguages() {
		languages.clear();
		for (TranslatedPlugin translatedPlugin : plugins.values())
			for (String lang : translatedPlugin.getTranslations().keySet())
				if (!languages.contains(lang))
					languages.add(lang);
	}

}
