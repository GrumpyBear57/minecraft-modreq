package com.grumpybear.modreq;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class modreq extends JavaPlugin {
	
@Override
public void onEnable() {
	getLogger().info("Successfully loaded modreq v1.0!");
}

@Override
public void onDisable() {
	getLogger().info("Unloading modreq v1.0...");
}

public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if(cmd.getName().equalsIgnoreCase("modreq")) {
		sender.sendMessage("debug info");
	}
	
	if(cmd.getName().equalsIgnoreCase("modqueue")) {
		sender.sendMessage("debug info");
	}
	
	if(cmd.getName().equalsIgnoreCase("reqaccept")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("reqresolve")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("reqclose")) {
		sender.sendMessage("debug info");
	}
	
	if(cmd.getName().equalsIgnoreCase("reqabandon")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("reqesc")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("reqtp")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("reqnote")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("reqstatus")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("modstats")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("modmode")) {
		sender.sendMessage("debug info");
	}
	return false;
}

}
 