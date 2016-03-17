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

import java.util.UUID;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 * 
 * @author Jakub Sapalski
 * @version 1.0.0
 */
public abstract class Database {

	protected Database() throws DatabaseException {
	}

	/**
	 * Updates the database with player's UUID and language.
	 * 
	 * @param uuid
	 *            UUID of the player
	 * @param lang
	 *            new language
	 */
	public abstract void update(UUID uuid, String lang);

	/**
	 * Loads language for specified player from the database.
	 * 
	 * @param uuid
	 *            UUID of the player
	 * @return player's language
	 */
	public abstract String load(UUID uuid);

	/**
	 * Closes all resources used by the database.
	 */
	public abstract void finish();

}