package com.grumpybear.modreq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class main extends JavaPlugin {
	
	private SQLManager sql;
	
	@Override
	public void onEnable() {
		initDatabase();
	}
	
	@Override
	public void onDisable() {
		sql.onDisable();
	}
	
	private void initDatabase() {
		sql = new SQLManager(this);
	}
	
	public SQLManager getSQLManager() {
		return sql;
	}
	
	public class ConnectionPoolManager {

		private final main plugin;
		
		private HikariDataSource dataSource;
		private String hostname, port, database, username, password, testQuery;
		private int minConnections, maxConnections;
		private long connectionTimeout;
		
		public ConnectionPoolManager(main plugin) {
			this.plugin = plugin;
			init();
			setupPool();
		}
		
		private void init() {
			hostname = plugin.getConfig().getString("sql.hostname");
	        port = plugin.getConfig().getString("sql.port");
	        database = plugin.getConfig().getString("sql.database");
	        username = plugin.getConfig().getString("sql.username");
	        password = plugin.getConfig().getString("sql.password");
	        minConnections = plugin.getConfig().getSQLPoolMinConnections();
	        maxConnections = plugin.getConfig().getSQLPoolMaxConnections();
	        connectionTimeout = plugin.getConfig().getSQLPoolConnectionTimeoutMillis();
	        testQuery = plugin.getConfig().getSQLPoolTestQuery();
		}
		
		private void setupPool() {
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl("jbdc:mysql://" + hostname + ":" + port + "/" + database);
			config.setDriverClassName("com.mysql.jbdc.Driver");
			config.setUsername(username);
			config.setPassword(password);
			config.setMinimumIdle(minConnections);
			config.setMaximumPoolSize(maxConnections);
			config.setConnectionTimeout(connectionTimeout);
			config.setConnectionTestQuery(testQuery);
			dataSource = new HikariDataSource(config);
		}
		
		public Connection getConnection() throws SQLException {
			return dataSource.getConnection();
		}
		
		public void close(Connection conn, PreparedStatement ps, ResultSet res) {
			if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
			if (ps != null) try { ps.close(); } catch (SQLException ignored) {} 
			if (res != null) try { res.close(); } catch (SQLException ignored) {} 
		}
		
		public void closePool() {
			if (dataSource != null && !dataSource.isClosed()) dataSource.close();
		}
		
	}
	
	public class SQLManager {

		private HikariDataSource dataSource;
		private final main plugin;
		private final ConnectionPoolManager pool;
		
		public SQLManager(main plugin) {
			this.plugin = plugin;
			pool = new ConnectionPoolManager(plugin);
			makeTable();
		}
		
		private void makeTable() {
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = dataSource.getConnection();
				ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS 'Test' " + "(" + "UUID varchar(30)" + ")");
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				pool.close(conn, ps, null);
			}
			
		}
		
		public void onDisable() {
			pool.closePool();
		}

	}

	
}


