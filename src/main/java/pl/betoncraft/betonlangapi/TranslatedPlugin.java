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

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

/**
 * This class represents a translated plugin. It holds information about the
 * default language specified by the author of the plugin, handles converting
 * language codes to the unified ones and holds all translations of the plugin.
 * 
 * @author Jakub Sapalski
 * @version 1.0.0
 */
public class TranslatedPlugin {

	private HashMap<String, HashMap<String, String>> translations = new HashMap<>();

	private Plugin plugin;
	private TranslationLoader translationLoader;
	private String defaultLang;
	private ArrayList<String> languages = new ArrayList<>();

	/**
	 * Creates a new object representing translation of the plugin. To use this
	 * constructor you have to have a single "messages.yml" file containing all
	 * languages. Each language should be specified as a single section with
	 * language code as the key.
	 * 
	 * @param plugin
	 *            the plugin which is translated
	 * @param defaultLang
	 *            default language which should be used if there is no
	 *            translation for player's language; set this to the language in
	 *            which your plugin is written, to ensure that all messages are
	 *            available in this language
	 */
	public TranslatedPlugin(Plugin plugin, String defaultLang) {
		this(plugin, TranslationLoader.getDefaultFileLoader(), defaultLang);
	}

	/**
	 * Full constructor, which allows you to specify all logic responsible for
	 * loading translated messages into the plugin and displaying them correctly
	 * to the player. Refer to {@link TranslationLoader} documentation for more
	 * info on how to create instance of this interface.
	 * 
	 * @param plugin
	 *            the plugin which is translated
	 * @param translationLoader
	 *            TranslationLoader instance, used to load data from files
	 *            containing translations
	 * @param defaultLang
	 *            default language which should be used if there is no
	 *            translation for player's language; set this to the language in
	 *            which your plugin is written, to ensure that all messages are
	 *            available in this language
	 */
	public TranslatedPlugin(Plugin plugin, TranslationLoader translationLoader, String defaultLang) {
		this.plugin = plugin;
		this.translationLoader = translationLoader;
		this.defaultLang = defaultLang;
		load();
	}

	/**
	 * Loads all messages into this object.
	 */
	public void load() {
		translations.clear();
		HashMap<String, HashMap<String, String>> data = translationLoader.loadTranslations(plugin);
		for (String lang : data.keySet()) {
			HashMap<String, String> translation = data.get(lang);
			String rawBindings = BetonLangAPI.getInstance().getConfig()
					.getString("languages." + plugin.getName() + "." + lang);
			if (rawBindings == null) {
				BetonLangAPI.getInstance().getConfig().set("languages." + plugin.getName() + "." + lang, lang);
				BetonLangAPI.getInstance().saveConfig();
				rawBindings = lang;
			}
			String[] bindings = rawBindings.split(",");
			for (String binding : bindings) {
				binding = binding.trim();
				translations.put(binding, translation);
				if (!languages.contains(binding)) {
					languages.add(binding);
				}
			}
		}
	}

	/**
	 * Returns the default language used by this plugin.
	 * 
	 * @return default language code
	 */
	public String getDefaultLang() {
		return defaultLang;
	}

	/**
	 * Returns the map containing all translations.
	 * 
	 * @return translations
	 */
	public HashMap<String, HashMap<String, String>> getTranslations() {
		return translations;
	}

	/**
	 * Retrieves specified message in specified language. It replaces variables
	 * with specified objects and converts color codes.
	 * 
	 * @param lang
	 *            language in which the message will be retrieved; if there is
	 *            no message in this language, it will use default language; if
	 *            there are no message at all it will return null
	 * @param name
	 *            name of the message
	 * @param variables
	 *            variables to insert into the message
	 * @return the message
	 */
	public String getMessage(String lang, String name) {
		String message;
		HashMap<String, String> translation = translations.get(lang);
		if (translation != null) {
			message = translation.get(name);
			if (message != null) {
				return message;
			}
		}
		translation = translations.get(defaultLang);
		if (translation != null) {
			message = translation.get(name);
			if (message != null) {
				return message;
			}
		}
		return null;
	}

}
