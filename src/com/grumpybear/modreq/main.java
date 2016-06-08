package com.grumpybear.modreq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.zaxxer.hikari.HikariDataSource;

public class main extends JavaPlugin implements Listener {
	
	private HikariDataSource hikari;
	
	@Override
	public void onEnable() {
		connectDB();
	}
	
	@Override
	public void onDisable() {
	}
	
	public void connectDB() {
		String address = getConfig().getString("Database.Address");
		String name = getConfig().getString("Database.Name");
		String username = getConfig().getString("Database.Username");
		String password = getConfig().getString("Database.Password");
		
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
		Connection connection = null;
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
		
		PreparedStatement p = null;
		
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
	
}


