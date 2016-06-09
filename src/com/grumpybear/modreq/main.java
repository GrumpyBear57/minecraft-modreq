package com.grumpybear.modreq;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.zaxxer.hikari.HikariDataSource;

public class main extends JavaPlugin implements Listener {
	// version
	String version = "v1.0.0";

	// colours
	ChatColor GREEN = ChatColor.GREEN;
	ChatColor GOLD = ChatColor.GOLD;
	ChatColor RED = ChatColor.RED;
	
	// chat output vars
	String prefix = RED + "[" + GREEN + "Mod Request" + RED + "] " + GOLD;
	String noPerm = RED + "You don't have permission to perform that command!";
	String notPlayer = "You must be a player to perform this command!";
	
	// database stuffs  
	private HikariDataSource hikari;
	PreparedStatement p = null;
	Connection connection = null;
	
	// other stuff
	FileConfiguration config = getConfig();
	Logger log = getLogger();
	
	@Override
	public void onEnable() {
		createConfig();
		// DO EVERYTHING NOT DATABSE RELATED BEFORE THIS
		connectDB();
		createTable();
		log.info("Successfully loaded modreq " + version);
	}
	
	@Override
	public void onDisable() {
	}
	
	private void createConfig() {
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			File file = new File(getDataFolder(), "config.yml");
			if (!file.exists()) {
				log.info("config.yml doesn't exist. Creating...");
				config.addDefault("Database.Address", "localhost");
				config.addDefault("Database.Name", "database");
				config.addDefault("Database.Username", "AzureDiamond");
				config.addDefault("Database.Password", "hunter2");
				config.addDefault("", "");
				config.addDefault("##View docs", "<LINK>"); //TODO add link to documentation about plugin (contains things like permission nodes)
				config.options().copyDefaults(true);
				saveConfig();
			} else {
				log.info("Loading config...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void connectDB() {
		String address = config.getString("Database.Address");
		String name = config.getString("Database.Name");
		String username = config.getString("Database.Username");
		log.info("connecting to database " + name + " on " + address + " with user " + username + "...");
		String password = config.getString("Database.Password");
		
		hikari = new HikariDataSource();
		hikari.setMaximumPoolSize(8);
		hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		hikari.addDataSourceProperty("serverName", address);
		hikari.addDataSourceProperty("port", 3306);
		hikari.addDataSourceProperty("databaseName", name);
		hikari.addDataSourceProperty("user", username);
		hikari.addDataSourceProperty("password", password);
		log.info("Database connected.");
	}

	public void createTable() {
		String createTable = "CREATE TABLE IF NOT EXISTS requests " +
				  "(id INT NOT NULL AUTO_INCREMENT, " +
				  "user VARCHAR(32) NOT NULL, "+
				  "status VARCHAR(10) NOT NULL, " +
				  "assignee VARCHAR(32) NOT NULL, " +
				  "time_submitted DATETIME NOT NULL, " +
				  "time_resolved DATETIME NULL, " +
				  "location_x DOUBLE NOT NULL, " +
				  "location_y DOUBLE NOT NULL, " +
				  "location_z DOUBLE NOT NULL, " +
				  "location_yaw FLOAT NOT NULL, " +
				  "location_pitch FLOAT NOT NULL, " +
				  "request VARCHAR(100) NOT NULL, " +
				  "note_x VARCHAR(100) NULL, " + //TODO figure out a way to get this to create as many note colums as config specifies
				  "resolution VARCHAR(100) NULL, " +
				  "escalated TINYINT NULL, " +
				  "PRIMARY KEY (id)) ";
		
		try {
			connection = hikari.getConnection();
			p = connection.prepareStatement(createTable);
			p.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (p != null) {
				try {
					p.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public class commandModreq implements CommandExecutor {

		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (args.length == 0) {
				sender.sendMessage(prefix + GOLD + "This server is running " + GREEN + "Mod Request " + version + GOLD + " by GrumpyBear57!");
				sender.sendMessage(GOLD + "To submit a request, do /modreq <request>");
				sender.sendMessage("Licensed under Apache v2.0, Copyright 2016 GrumpyBear57");
			//} else if (!(sender instanceof Player)) { 
				//sender.sendMessage(notPlayer); //TODO find out if this works
			} else { 
				Player player = (Player) sender;
				int ticketNumber = 42; //this is temporary until we get the request database going.
				
				if(player.hasPermission("modreq.newReq")) {
					String request = null;
					for (int i = 0; i < args.length; i++) {
						if (i != args.length-1) {
							request += args[i] + " ";
						} else {
							request += args[i];
						}
					}
					String query = ""; //TODO actual query
					
					try {
						connection = hikari.getConnection();
						p = connection.prepareStatement(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					sender.sendMessage(prefix + "Your request has been added to the queue. Your ticket number is " + ticketNumber + ".");
					sender.sendMessage(GOLD + "Your request: '" + request + "'");
					
					Player[] playersOnline = Bukkit.getServer().getOnlinePlayers().toArray(new Player[Bukkit.getServer().getOnlinePlayers().size()]);
					for (int i = 0; i < playersOnline.length; i++) {
						Player player1 = (Player) playersOnline[i];
						if (player1.hasPermission("modreq.veiwQueue")) {
							player1.sendMessage(prefix + GREEN + player.getDisplayName() + GOLD + " has submitted a new request!");
						}
					}
				} else {
					sender.sendMessage(noPerm);
				}
			}
			return false;
		}
		
	}
	
}
