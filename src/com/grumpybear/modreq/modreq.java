package com.grumpybear.modreq;

import java.io.File;

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
Boolean newRequests = true;
	
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
			config.addDefault("test", true);
			config.options().copyDefaults(true);
			saveConfig();
		}else{
			getLogger().info("Config found, loading!");
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	
	// Announce our presence
	getLogger().info("Successfully loaded modreq " + version);
	Bukkit.broadcastMessage("Successfully loaded modreq " + version);
}

@Override
public void onDisable() {
	getLogger().info("Unloading modreq " + version);
}

public class staffJoinListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("modreq.viewQueue") && newRequests) {
			player.sendMessage(ChatColor.RED + "[" + ChatColor.GREEN + "Mod Request" + ChatColor.RED + "] " + 
			ChatColor.GOLD + "New (request(s) in queue!");
		}
	}
}

public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if(cmd.getName().equalsIgnoreCase("modreq")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender; 
			if(player.hasPermission("modreq.newReq")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}
	
	if(cmd.getName().equalsIgnoreCase("modqueue")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.viewQueue")) {
			sender.sendMessage("Items in queue");
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
		}
	}
	
	if(cmd.getName().equalsIgnoreCase("reqaccept")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender;
			if(player.hasPermission("modreq.newReq")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqresolve")) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to perform this command!");
		}else{
			Player player = (Player) sender;
			if(player.hasPermission("modreq.newReq")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqclose")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.newReq")) {
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
			if(player.hasPermission("modreq.newReq")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqesc")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.newReq")) {
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
			if(player.hasPermission("modreq.newReq")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqnote")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.newReq")) {
			sender.sendMessage("has permission.");
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
		}
	}

	if(cmd.getName().equalsIgnoreCase("reqstatus")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.newReq")) {
			sender.sendMessage("has permission.");
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
		}
	}

	if(cmd.getName().equalsIgnoreCase("modstats")) {
		Player player = (Player) sender;
		if(player.hasPermission("modreq.newReq")) {
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
			if(player.hasPermission("modreq.newReq")) {
				sender.sendMessage("has permission.");
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform that command!");
			}
		}
	}
	return false;
}

}
 