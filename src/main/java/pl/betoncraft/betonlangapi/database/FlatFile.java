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
package pl.betoncraft.betonlangapi.database;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import pl.betoncraft.betonlangapi.BetonLangAPI;

/**
 * Stores data in a "data.yml" file in the plugin's main directory.
 * 
 * @author Jakub Sapalski
 * @version 1.0.0
 */
public class FlatFile extends Database {

	private File file;
	private YamlConfiguration config;

	public FlatFile() throws DatabaseException {
		super();
		file = new File(BetonLangAPI.getInstance().getDataFolder(), "data.yml");
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new DatabaseException("Could not create database file: " + e.getMessage());
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public synchronized void update(UUID uuid, String lang) {
		config.set(uuid.toString(), lang);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String load(UUID uuid) {
		return config.getString(uuid.toString());
	}

	@Override
	public void finish() {
		// do nothing here
	}

}
