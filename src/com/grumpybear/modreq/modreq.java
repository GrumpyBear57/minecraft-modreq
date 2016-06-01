package com.grumpybear.modreq;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler; 
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;

public class modreq extends JavaPlugin {
	
@Override
public void onEnable() {
	getLogger().info("Successfully loaded modreq v1.0!");
	getServer().getPluginManager().registerEvents(new staffJoinListener(), this);
}

@Override
public void onDisable() {
	getLogger().info("Unloading modreq v1.0...");
}

public class staffJoinListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Bukkit.broadcastMessage("New request(s) in queue!");
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
		if(player.hasPermission("modreq.newReq")) {
			sender.sendMessage("has permission.");
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
 