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

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Handles language and reload commands.
 * 
 * @author Jakub Sapalski
 */
public class Commands implements CommandExecutor {

	private final Permission VIEW_PERM = new Permission("betonlangapi.view");
	private final Permission SELF_PERM = new Permission("betonlangapi.self");
	private final Permission OTHER_PERM = new Permission("betonlangapi.other");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("language")) {
			switch (args.length) {
			case 0:
				if (!sender.hasPermission(VIEW_PERM))
					return sendMessage(sender, "no_permission");
				String available = String.join(" / ", BetonLangAPI.getLanguages().stream()
						.map(lang -> BetonLangAPI.getMessage(lang, BetonLangAPI.getInstance(), "available_langs"))
						.collect(Collectors.toList()));
				sender.sendMessage(ChatColor.YELLOW + available + ":");
				for (String lang : BetonLangAPI.getLanguages()) {
					sender.sendMessage(ChatColor.AQUA + "/" + label + " " + lang + ChatColor.GREEN + " - " +
							BetonLangAPI.getMessage(lang, BetonLangAPI.getInstance(), "lang_name"));
				}
				return true;
			case 1:
				if (!sender.hasPermission(SELF_PERM))
					return sendMessage(sender, "no_permission");
				if (!(sender instanceof Player))
					return sendMessage(sender, "player_only");
				String lang1 = args[0];
				if (!BetonLangAPI.getLanguages().contains(lang1))
					return sendMessage(sender, "no_language");
				BetonLangAPI.setLanguage(((Player) sender), lang1);
				return sendMessage(sender, "lang_changed");
			case 2:
				if (!sender.hasPermission(OTHER_PERM))
					return sendMessage(sender, "no_permission");
				String lang2 = args[1];
				if (!BetonLangAPI.getLanguages().contains(lang2))
					return sendMessage(sender, "no_language");
				@SuppressWarnings("deprecation")
				Player player = Bukkit.getPlayer(args[0]);
				if (player == null)
					return sendMessage(sender, "player_offline");
				BetonLangAPI.setLanguage(player, lang2);
				return sendMessage(sender, "lang_changed");
			default:
			}
		} else if (cmd.getName().equalsIgnoreCase("betonlangapi")) {
			BetonLangAPI.reloadPlugin();
			return sendMessage(sender, "reloaded");
		}
		return false;
	}

	/**
	 * Sends message to the CommandSender.
	 * 
	 * @param sender
	 *            target of this message
	 * @param message
	 *            message name
	 * @return always true
	 */
	private boolean sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				BetonLangAPI.getMessage(sender, BetonLangAPI.getInstance(), message)));
		return true;
	}

}
