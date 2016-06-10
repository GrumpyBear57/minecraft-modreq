package com.grumpybear.modreq;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.zaxxer.hikari.HikariDataSource;

public class main extends JavaPlugin implements Listener {
	// version
	String version = "v1.0.0";

	// colours
	ChatColor GREEN = ChatColor.GREEN;
	ChatColor GOLD = ChatColor.GOLD;
	ChatColor RED = ChatColor.RED;
	ChatColor AQUA = ChatColor.AQUA;
	
	// chat output vars
	String prefix = RED + "[" + GREEN + "Mod Request" + RED + "] " + GOLD;
	String noPerm = RED + "You don't have permission to perform that command!";
	String notPlayer = RED + "You must be a player to perform this command!";
	
	// database stuffs  
	private HikariDataSource hikari;
	Connection connection = null;
	
	// other stuff
	FileConfiguration config = getConfig();
	Logger log = getLogger();
	
	@Override
	public void onEnable() {
		createConfig();
		// initialize stuff
		log.info("Registering commands...");
		this.getCommand("modreq").setExecutor(new commandModreq());
		this.getCommand("modqueue").setExecutor(new commandModqueue());
		this.getCommand("reqaccept").setExecutor(new commandReqaccept());
		
		getServer().getPluginManager().registerEvents(new staffJoinListener(), this);
		
		// DO EVERYTHING NOT DATABSE RELATED BEFORE THIS
		connectDB();
		createTable();
		log.info("Successfully loaded modreq " + version);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Unloading modreq " + version);
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
	
	public class staffJoinListener implements Listener {
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			PreparedStatement p = null;
			Player player = event.getPlayer(); 
			if (player.hasPermission("modreq.viewQueue")) {
				String query = "SELECT id,name FROM requests WHERE status='OPEN'";
				try {
					connection = hikari.getConnection();
					p = connection.prepareStatement(query);
					ResultSet rs = p.executeQuery();
					if (rs.next()) {
						player.sendMessage(prefix + "New request(s) in queue!");
					}
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
		PreparedStatement p = null;
		String createTable = "CREATE TABLE IF NOT EXISTS requests " +
				  "(id INT NOT NULL AUTO_INCREMENT, " +
				  "user VARCHAR(36) NOT NULL, " +
				  "name VARCHAR(32) NOT NULL, " +
				  "status VARCHAR(10) NOT NULL, " +
				  "assignee VARCHAR(36) NULL, " +
				  "assignee_name VARCHAR(32) NULL, " +
				  "time_submitted DATETIME NOT NULL, " +
				  "time_resolved DATETIME NULL, " +
				  "location VARCHAR(128) NOT NULL, " +
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
	
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public class commandModreq implements CommandExecutor {
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			PreparedStatement p = null;
			Player player = (Player) sender;
			if (args.length == 0) {
				sender.sendMessage(prefix + GOLD + "This server is running " + GREEN + "Mod Request " + version + GOLD + " by GrumpyBear57!");
				if (player.hasPermission("modreq.newReq")) {
					sender.sendMessage(GOLD + "To submit a request, do /modreq <request>");
				}
				sender.sendMessage(AQUA + "Licensed under Apache v2.0, Copyright 2016 GrumpyBear57");
			} else { 
				if (!(sender instanceof Player)) {
					sender.sendMessage(notPlayer);
				} else {
					if(player.hasPermission("modreq.newReq")) {
						String request = "";
						for (int i = 0; i < args.length; i++) {
							if (i != args.length-1) {
								request += args[i] + " ";
							} else {
								request += args[i];
							}
						}
						String insert = "INSERT INTO requests (user, name, status, time_submitted, location, request) " + 
						"VALUES (?, ?, 'open', now(), ?, ?)";
						
						String UUID = ((Player) sender).getUniqueId().toString();
						String name = ((Player) sender).getDisplayName();
						String location = ((Player) sender).getLocation().toString(); // might have issues with this later... 

						try {
							connection = hikari.getConnection();
							p = connection.prepareStatement(insert);
							p.setString(1, UUID);
							p.setString(2, name);
							p.setString(3, location);
							p.setString(4, request);
							p.execute();
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							if (connection != null) {
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
						String getID = "SELECT id FROM requests WHERE user=? AND location=? AND request=?";
						try {
							connection = hikari.getConnection();
							p = connection.prepareStatement(getID);
							p.setString(1, UUID);
							p.setString(2, location);
							p.setString(3, request);
							ResultSet rs = p.executeQuery();
							while (rs.next()) {
								int id = rs.getInt("id");
								sender.sendMessage(prefix + "Your request has been added to the queue.");
								sender.sendMessage(GOLD + "Your request ID is " + AQUA + "#" + id + GOLD + ".");
								sender.sendMessage(GOLD + "Your request: '" + AQUA + request + GOLD + "'");
								Player[] playersOnline = Bukkit.getServer().getOnlinePlayers().toArray(new Player[Bukkit.getServer().getOnlinePlayers().size()]);
								for (int i = 0; i < playersOnline.length; i++) {
									Player player1 = (Player) playersOnline[i];
									if (player1.hasPermission("modreq.veiwQueue")) {
										player1.sendMessage(prefix + AQUA + name + GOLD + " has submitted a new request with ID " + AQUA + "#" + id + GOLD + "!");
									}
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(noPerm);
					}
				}
			}
			return true;
		}
	}
	
	public class commandModqueue implements CommandExecutor {
		@Override 
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			PreparedStatement p = null;
			Player player = (Player) sender;
			if (player.hasPermission("modreq.admin")) {
				if (!(args.length == 0)) {
					sender.sendMessage(RED + "This command doesn't take arguements!");
				} else {
					String query = "SELECT id,name,status FROM requests WHERE status IN ('OPEN', 'PENDING', 'ESCALATED')";
					try {
						connection = hikari.getConnection();
						p = connection.prepareStatement(query);
						ResultSet rs = p.executeQuery();
						if (rs.next()) {
							sender.sendMessage(prefix + "Open requests:");
							int id = rs.getInt("id");
							String name = rs.getString("name");
							String status = rs.getString("status");
							sender.sendMessage(GOLD + "Request ID: " + AQUA + id + GOLD + " from " + AQUA + name + GOLD + " with status: " + status + ".");
							while (rs.next()) {
								int id1 = rs.getInt("id");
								String name1 = rs.getString("name");
								String status1 = rs.getString("status");
								sender.sendMessage(GOLD + "Request ID: " + AQUA + id1 + GOLD + " from " + AQUA + name1 + GOLD + " with status: " + status1 + ".");
							}
						}
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
			} else if (player.hasPermission("modreq.viewQueue")) {
				if (!(args.length == 0)) {
					sender.sendMessage(RED + "This command doesn't take arguements!");
				} else {
					String query = "SELECT id,name FROM requests WHERE status='OPEN'";
					try {
						connection = hikari.getConnection();
						p = connection.prepareStatement(query);
						ResultSet rs = p.executeQuery();
						if (rs.next()) {
							sender.sendMessage(prefix + "Open requests:");
							int id = rs.getInt("id");
							String name = rs.getString("name");
							sender.sendMessage(GOLD + "Request ID: " + AQUA + id + GOLD + " from " + AQUA + name + GOLD + ".");
							while (rs.next()) {
								int id1 = rs.getInt("id");
								String name1 = rs.getString("name");
								
								sender.sendMessage(GOLD + "Request ID: " + AQUA + id1 + GOLD + " from " + AQUA + name1 + GOLD + ".");
							}
						} else {
							sender.sendMessage(prefix + "No items in queue!");
						}
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
			} else {
				sender.sendMessage(noPerm);
			}
			return true;
		}
	}
	
	public class commandReqaccept implements CommandExecutor {
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			PreparedStatement p = null;
			Player player = (Player) sender; 
			if (player.hasPermission("modreq.reqaccept")){
				if (args.length == 0 || args.length > 1) {
					sender.sendMessage(RED + "Please enter a valid request ID!");
				} else if (!(isInt(args[0]))) {
					sender.sendMessage(RED + "Please enter a valid request ID!");
				} else  if (!(sender instanceof Player)) {
					sender.sendMessage(notPlayer);
				} else {
					int id = Integer.parseInt(args[0]);
					String UUID = ((Player) sender).getUniqueId().toString();
					String name = ((Player) sender).getDisplayName();
					
					String query = "UPDATE requests SET status='pending', assignee='" + UUID + "', assignee_name='" + name + "' WHERE id=?";
					
					try {
						connection = hikari.getConnection();
						p = connection.prepareStatement(query);
						p.setInt(1, id);
						p.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (connection != null) {
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
					sender.sendMessage(prefix + "Successfully accepted request " + AQUA + "#" + id + GOLD + "!"); 
					//TODO maybe a way to validate they did actually accept the ticket? 
					//If one where to enter in a ticket number that doesn't exist, this would still trigger.
				}
			} else {
				sender.sendMessage(noPerm);
			}
			return true;
		}
	}
	
}
