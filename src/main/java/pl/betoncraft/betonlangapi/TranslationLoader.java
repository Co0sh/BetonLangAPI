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
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * <p>
 * This interface should be used to provide a HashMap containing all
 * translations every time the {@link #loadTranslations(Plugin)} method is
 * called.
 * </p>
 * 
 * <p>
 * If you don't want to write your own implementation of this interface you can
 * use {@link #getDefaultFileLoader()} method to obtain default
 * TranslationLoader.
 * 
 * @author Jakub Sapalski
 * @version 1.0.0
 */
public interface TranslationLoader {

	/**
	 * This method must return a HashMap, where the key is language code and the
	 * value is another HashMap. This one should have message name as the key
	 * and translation as the value.
	 * 
	 * @param plugin
	 *            plugin which owns the messages
	 * @return the HashMap containing translations
	 */
	public HashMap<String, HashMap<String, String>> loadTranslations(Plugin plugin);

	/**
	 * This method returns a default TranslationLoader, which will load
	 * "messages.yml" file located in plugin's main directory. Every language
	 * should be specified as a separate section with language code as section
	 * name.
	 * 
	 * @return the default TranslationLoader
	 */
	public static TranslationLoader getDefaultFileLoader() {
		return new TranslationLoader() {
			@Override
			public HashMap<String, HashMap<String, String>> loadTranslations(Plugin plugin) {
				HashMap<String, HashMap<String, String>> translations = new HashMap<>();
				File file = new File(plugin.getDataFolder(), "messages.yml");
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
				for (String lang : yaml.getKeys(false)) {
					HashMap<String, String> translation = new HashMap<>();
					for (String key : yaml.getConfigurationSection(lang).getKeys(false)) {
						translation.put(key, yaml.getString(lang + "." + key));
					}
					translations.put(lang, translation);
				}
				return translations;
			}
		};
	}

}
