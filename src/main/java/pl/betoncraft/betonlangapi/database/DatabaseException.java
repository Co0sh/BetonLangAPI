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

/**
 * Thrown when there are any errors with instantiating Database implementation.
 * 
 * @author Jakub Sapalski
 * @version 1.0.0
 */
public class DatabaseException extends Exception {

	private static final long serialVersionUID = -2356301473220048L;
	
	private final String message;
	
	/**
	 * Creates new DatabaseException with specified message.
	 * 
	 * @param message
	 */
	public DatabaseException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
