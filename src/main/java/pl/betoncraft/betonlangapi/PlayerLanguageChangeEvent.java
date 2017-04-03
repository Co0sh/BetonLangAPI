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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is fired when the language of the player is about to change.
 * 
 * @author Jakub Sapalski
 * @version 1.0.0
 */
public class PlayerLanguageChangeEvent extends Event implements Cancellable {

	private final static HandlerList handlers = new HandlerList();

	private final Player player;
	private final String language;

	private boolean cancelled = false;

	/**
	 * Creates new PlayerLanguageChangeEvent instance.
	 * 
	 * @param player
	 *            player whose language changes
	 * @param language
	 *            new language
	 */
	public PlayerLanguageChangeEvent(Player player, String language) {
		this.player = player;
		this.language = language;
	}

	/**
	 * @return player whose language changes
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return new language
	 */
	public String getLanguage() {
		return language;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
