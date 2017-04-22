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
package pl.betoncraft.betonlangapi.placeholderapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

/**
 * This class handles PlaceholderAPI integration by registering the "lang"
 * placeholder and resolving placeholders in the translatable messages.
 *
 * @author Jakub Sapalski
 */
public class PlaceholderHandler {

	private boolean active = false;

	public PlaceholderHandler() {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			active = true;
			new LangPlaceholder().hook();
		}
	}

	public String resolve(Player player, String string) {
		return active ? PlaceholderAPI.setPlaceholders(player, string) : string;
	}

}
