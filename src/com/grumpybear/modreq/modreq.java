package com.grumpybear.modreq;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
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
String version = "v1.0";
String prefix = ChatColor.RED + "[" + ChatColor.GREEN + "Mod Request" + ChatColor.RED + "] " + ChatColor.GOLD;
Boolean newRequests = true; // this is temporary until we get the database

// database things
private Connection connection; 
private String host, database, username, password;
private int port;
	
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
			config.addDefault("host", "localhost");
			config.addDefault("port", 3306);
			config.addDefault("database", "requests");
			config.addDefault("username", "username");
			config.addDefault("password", "password");
			config.options().copyDefaults(true);
			saveConfig();
		}else{
			getLogger().info("Config found, loading!");
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	
	// Connect to the database 
	host = config.getString("host");
	port = config.getInt("port");
	database = config.getString("database");
	username = config.getString("username");
	password = config.getString("password");
	
	try {
		openConnection();
		Statement statement = connection.createStatement();
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (SQLException e ) {
		e.printStackTrace();
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
		connection = DriverManager.getConnection("jbdc:mysql://" + this.host + ":" +  this.port + "/" + this.database, this.username, this.password);
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
	return false;
}
}


 
