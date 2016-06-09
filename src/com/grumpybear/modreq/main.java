package com.grumpybear.modreq;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	
	// chat output vars
	String prefix = ChatColor.RED + "[" + ChatColor.GREEN + "Mod Request" + ChatColor.RED + "] " + ChatColor.GOLD;
	String noPerm = ChatColor.RED + "You don't have permission to perform that command!";
	
	// database stuffs  
	private HikariDataSource hikari;
	PreparedStatement p = null;
	Connection connection = null;
	
	// config file stuffs
	private File configf, databasef;
	private FileConfiguration config, database;
	
	@Override
	public void onEnable() {
		getLogger().info("Connecting to database...");
		connectDB();
		getLogger().info("Database connected.");
		createTable();
		createConfig();
		getLogger().info("Successfully loaded modreq " + version);
	}
	
	@Override
	public void onDisable() {
	}
	
	public FileConfiguration getDatabaseConfig() {
		return this.database;
	}
	
	private void createConfig() {
		configf = new File(getDataFolder(), "config.yml");
		databasef = new File(getDataFolder(), "database.yml");
		
		if (!configf.exists()) {
			getLogger().info("config.yml doesn't exist... creating...");
			configf.getParentFile().mkdirs();
			saveResource("config.yml");
		}
		if (!databasef.exists()) {
			getLogger().info("database.yml doesn't exist... creating...");
			databasef.getParentFile().mkdirs();
			saveResource("database.yml");
		}
		
		config = new YamlConfiguration();
		database = new YamlConfiguration();
		try {
			config.load(configf);
			database.load(databasef);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connectDB() {
		String address = plugin.getDatabaseConfig().getString("Database.Address");
		String name = plugin.getDatabaseConfig().getString("Database.Name");
		String username = plugin.getDatabaseConfig().getString("Database.Username");
		String password = plugin.getDatabaseConfig().getString("Database.Password");
		
		hikari = new HikariDataSource();
		hikari.setMaximumPoolSize(8);
		hikari.setDataSourceClassName("com.mysql.jbdc.jbdc2.optional.MysqlDataSource");
		hikari.addDataSourceProperty("serverName", address);
		hikari.addDataSourceProperty("port", 3306);
		hikari.addDataSourceProperty("databaseName", name);
		hikari.addDataSourceProperty("user", username);
		hikari.addDataSourceProperty("password", password);
	}

	public void createTable() {
		String createTable = "CREATE TABLE IF NOT EXISTS requests " +
				  "(id INT NOT NULL AUTO_INCREMENT, " +
				  "user VARCHAR(32) NOT NULL, "+
				  "status VARCHAR(10) NOT NULL, " +
				  "assignee VARCHAR(32) NOT NULL, " +
				  "time_submitted DATETIME GENERATED ALWAYS AS (NOW()) VIRTUAL, " +
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
				sender.sendMessage(prefix + ChatColor.GOLD + "This server is running " + ChatColor.GREEN + "Mod Request " + version + ChatColor.GOLD + " by GrumpyBear57!");
				sender.sendMessage(ChatColor.GOLD + "To submit a request, do /modreq <request>");
				sender.sendMessage("Licensed under Apache v2.0, Copyright 2016 GrumpyBear57");
			} else if (!(sender instanceof Player)) { 
				sender.sendMessage("You must be a player to perform this command!"); //TODO find out if this works
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
					sender.sendMessage(ChatColor.GOLD + "Your request: '" + request + "'");
					
					Player[] playersOnline = Bukkit.getServer().getOnlinePlayers().toArray(new Player[Bukkit.getServer().getOnlinePlayers().size()]);
					for (int i = 0; i < playersOnline.length; i++) {
						Player player1 = (Player) playersOnline[i];
						if (player1.hasPermission("modreq.veiwQueue")) {
							player1.sendMessage(prefix + ChatColor.GREEN + player.getDisplayName() + ChatColor.GOLD + " has submitted a new request!");
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
