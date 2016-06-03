package com.grumpybear.modreq;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;

public class modreq extends JavaPlugin {

FileConfiguration config = getConfig();
String version = "v1.0.0";
// version explanation x.y.z
// x = mc version (increases when we update to new mc version)
// y = main version (increases when we make significant update to plugin, like new command)
// z = fix version (increases when we make a minor change, like a bugfix)
String prefix = ChatColor.RED + "[" + ChatColor.GREEN + "Mod Request" + ChatColor.RED + "] " + ChatColor.GOLD;
Boolean newRequests = true; // this is temporary until we get the database

// database things
private Connection connection; 
private String host, database, username, password, table;
private int port, maxNotes;
	
@Override
public void onEnable() {
	// Enable listeners
	getServer().getPluginManager().registerEvents(new staffJoinListener(), this);
	
	// Config file things
	try {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			getLogger().info("Missing config file, creating!");
			config.addDefault("db-host", "localhost");
			config.addDefault("db-port", 3306);
			config.addDefault("db-name", "requests");
			config.addDefault("db-table", "requests");
			config.addDefault("db-username", "username");
			config.addDefault("db-password", "password");
			config.addDefault("max-notes", "10");
			//config.addDefault("//set this BEFORE the plugin creates a table for itself in your database!!");
			config.options().copyDefaults(true);
			saveConfig();
		}else{
			getLogger().info("Config found, loading!");
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	
	// Connect to the database 
	host = config.getString("db-host");
	port = config.getInt("db-port");
	database = config.getString("db-name");
	username = config.getString("db-username");
	password = config.getString("db-password");
	
	try {
		openConnection();
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (SQLException e ) {
		e.printStackTrace();
	}
	
	// test database to see if it's got a table for us, if not create it. 
	table = config.getString("db-table");
	maxNotes = config.getInt("max-notes");
	
	getLogger().info("Checking database...");
	Statement statement = null;
	
	try {
		DatabaseMetaData dbm = (DatabaseMetaData) connection.getMetaData();
		ResultSet checkTable = (ResultSet) dbm.getTables(null, null, this.table, null);
		if (!(checkTable.next())) {
			getLogger().info("Database table not found, creating one...");
			statement = (Statement) connection.createStatement();
			String createTable  = "CREATE TABLE requests " +
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
								  "note_x VARCHAR(100) NULL, " + //TODO add a loop to create as many note rows as the user configs 
								  "resolution VARCHAR(100) NULL, " +
								  "escalated TINYINT NULL, " +
								  "PRIMARY KEY (id)) ";
			statement.executeUpdate(createTable);
			getLogger().info("Database table created!");
			
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if(statement != null) {
				connection.close();
			}
		}catch (SQLException e){
			e.printStackTrace();
			}
		}
	
	// Announce our presence
	getLogger().info("Successfully loaded modreq " + version);
	Bukkit.broadcastMessage("Successfully loaded modreq " + version);
	if (newRequests) {
		getLogger().info("New request(s) in queue!");
	}
}

@Override
public void onDisable() {
	getLogger().info("Unloading modreq " + version);
}

public void openConnection() throws SQLException, ClassNotFoundException {
	if (connection != null && !connection.isClosed()) {
		return;
	}
	
	synchronized (this) {
		if (connection != null && !connection.isClosed()) {
			return;
		}
		Class.forName("com.mysql.jdbc.Driver");
		connection = (Connection) DriverManager.getConnection("jbdc:mysql://" + this.host + ":" +  this.port + "/" + this.database, this.username, this.password);
	}
}

public class staffJoinListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("modreq.viewQueue") && newRequests) {
			player.sendMessage(prefix + "New request(s) in queue!");
		}
	}
}

public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
	if(cmd.getName().equalsIgnoreCase("modreq")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else if(args.length == 0) {
			sender.sendMessage(prefix + ChatColor.GOLD + "This server is running " + ChatColor.GREEN + "Mod Request " + version + ChatColor.GOLD + " by GrumpyBear57!");
			sender.sendMessage(ChatColor.GOLD + "To submit a request, do /modreq <request>");
			sender.sendMessage("Licensed under Apache v2.0, Copyright 2016 GrumpyBear57");
		}else{
			Player player = (Player) sender; 
			int ticketNumber = 42; //this is temporary until we get the request database going. 
			// put the entirety of the request into one string.
			// TODO SANITIZE THE USER INPUT 
			if(player.hasPermission("modreq.newReq")) {
				String request = "";
				for(int i = 0; i < args.length; i++) {
					if (i != args.length-1) {
						request += args[i] + " ";
					}else{
						request += args[i];
					}
				}
				// Tell the user the request has been submitted
				sender.sendMessage(prefix + "Your request has been added to the queue. Your ticket number is " + ticketNumber + ".");
				sender.sendMessage(ChatColor.GOLD + "Your request: '" + request + "'");
				
				// Inform the staff online that there's a new request in the queue
				Player[] playersOnline = Bukkit.getServer().getOnlinePlayers().toArray(new Player[Bukkit.getServer().getOnlinePlayers().size()]);
				for (int i = 0; i < playersOnline.length; i++) {
					Player player1 = (Player) playersOnline[i];
					if (player1.hasPermission("modreq.veiwQueue")) {
						player1.sendMessage(prefix + ChatColor.GREEN + player.getDisplayName() + ChatColor.GOLD + " has submitted a new request!");
					}
				}
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
		return true;
	}
	
	if(cmd.getName().equalsIgnoreCase("modqueue")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.viewQueue")) {
			if (newRequests) {
				sender.sendMessage(prefix + "New request(s) in the queue!");
			}else{
				sender.sendMessage(prefix + "No new requests in the queue.");
			}
		}else{
			// show the user their open requests, if any.
		}
	}
	
	if(cmd.getName().equalsIgnoreCase("reqaccept")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender;
			if(player.hasPermission("modreq.reqAccept")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
		return true;
	}

	if(cmd.getName().equalsIgnoreCase("reqresolve")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender;
			if(player.hasPermission("modreq.reqAccept")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqclose")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.reqClose")) {
			sender.sendMessage("has permission.");
			//TODO add penalty for players abusing the modreq 
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
		}
	}
	
	if(cmd.getName().equalsIgnoreCase("reqabandon")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender;
			if(player.hasPermission("modreq.reqAccept")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqesc")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.reqAccept")) {
			sender.sendMessage("has permission.");
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqtp")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender;
			if(player.hasPermission("modreq.reqTeleport")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqnote")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.reqAccept")) {
			sender.sendMessage("has permission.");
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqstatus")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.reqStatus")) {
			sender.sendMessage("has permission.");
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
		}
	}

	if(cmd.getName().equalsIgnoreCase("modstats")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.modStats")) {
			sender.sendMessage("has permission.");
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
		}
	}

	if(cmd.getName().equalsIgnoreCase("modmode")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender;
			if(player.hasPermission("modreq.modMode")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}
	if (cmd.getName().equalsIgnoreCase("modreport")) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender;
			if (player.hasPermission("modreq.newReq")) {
				sender.sendMessage("has permission.");
				//TODO add penalty for a player abusing the report system (three stikes?)
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to permfor that commmand!");
			}
		}
	}
	return false;
}
}


 
