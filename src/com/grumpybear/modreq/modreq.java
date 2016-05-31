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
	
	if(cmd.getName().equalsIgnoreCase("modaccept")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("modresolve")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("modclose")) {
		sender.sendMessage("debug info");
	}
	
	if(cmd.getName().equalsIgnoreCase("modabandon")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("modesc")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("modtp")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("modnote")) {
		sender.sendMessage("debug info");
	}

	if(cmd.getName().equalsIgnoreCase("modstatus")) {
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
 