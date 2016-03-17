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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonlangapi.BetonLangAPI;

/**
 * Handles MySQL database type.
 * 
 * @author Jakub Sapalski
 * @version 1.0.0
 */
public class MySQL extends Database {

	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	private final Connection con;
	private final BukkitRunnable keeper;

	/**
	 * Creates a new MySQL instance.
	 * 
	 * @throws DatabaseException
	 */
	public MySQL(String hostname, String port, String database, String username, String password)
			throws DatabaseException {
		super();
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database,
					this.user, this.password);
			con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS betonlangapi (uuid CHAR(36) PRIMARY KEY"
					+ " NOT NULL, lang VARCHAR(36) NOT NULL);");
		} catch (SQLException | ClassNotFoundException e) {
			throw new DatabaseException("Could not connect to the database: " + e.getMessage());
		}
		keeper = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					con.createStatement().executeQuery("SELECT 1");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		keeper.runTaskTimerAsynchronously(BetonLangAPI.getInstance(), 60 * 20, 60 * 20);
	}

	@Override
	public synchronized void update(UUID uuid, String lang) {
		try {
			PreparedStatement stmt = con.prepareStatement("REPLACE INTO betonlangapi (uuid, lang) VALUES (?, ?);");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, lang);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String load(UUID uuid) {
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT lang FROM betonlangapi WHERE uuid = ?;");
			stmt.setString(1, uuid.toString());
			ResultSet res = stmt.executeQuery();
			if (res.next()) {
				return res.getString("lang");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void finish() {
		try {
			con.close();
			keeper.cancel();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
