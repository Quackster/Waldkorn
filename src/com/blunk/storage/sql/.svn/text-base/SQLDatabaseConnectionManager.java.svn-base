package com.blunk.storage.sql;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import com.blunk.Environment;
import com.blunk.Log;

/**
 * SQLDatabaseConnectionManager manages connections to a SQL database. This class is officially ripped from FUSELight for the hell of it.
 * 
 * @author Aapo Kyrola, Nillus
 */
public class SQLDatabaseConnectionManager implements Runnable
{
	private Object m_waitLock;
	
	private String m_epUrl;
	private String m_epUsername;
	private String m_epPassword;
	
	private int m_maxConnections;
	private int m_createdConnections;
	private Vector<Connection> m_availableConnections;
	
	private Thread m_monitorThread;
	
	public SQLDatabaseConnectionManager()
	{
		m_waitLock = new Object();
		m_availableConnections = new Vector<Connection>(0);
	}
	
	/**
	 * Prepares this instance of SQLDatabaseConnectionManager that will manage connections for a SQLDatabase specified in a properties file.
	 */
	public boolean prepare(String propsPath)
	{
		// Load properties
		Properties dbProps = new Properties();
		try
		{
			dbProps.load(new FileInputStream(propsPath));
		}
		catch (Exception ex)
		{
			Log.error("Could not load DatabaseConnectionManager properties file!", ex);
			return false;
		}
		
		// Set endpoint details
		m_epUrl = dbProps.getProperty("db.jdbc.url");
		m_epUsername = dbProps.getProperty("db.uid");
		m_epPassword = dbProps.getProperty("db.pwd");
		
		// Try load & register JDBC driver
		String driverClass = dbProps.getProperty("db.jdbc.driver");
		if (driverClass == null)
		{
			driverClass = "";
		}
		try
		{
			Driver connectionDriver = (Driver)Class.forName(driverClass).newInstance();
			DriverManager.registerDriver(connectionDriver);
		}
		catch (Exception ex)
		{
			Log.error("Could not load and/or register database connection driver '" + driverClass + "'", ex);
			return false;
		}
		
		// Parse max connections
		try
		{
			m_maxConnections = Integer.parseInt(dbProps.getProperty("db.maxconnections"));
			if (m_maxConnections < 0)
			{
				m_maxConnections = 0;
			}
		}
		catch (NumberFormatException ex)
		{
			m_maxConnections = 0;
		}
		
		// Initialize connection bucket
		m_availableConnections = new Vector<Connection>(m_maxConnections);
		
		// Test the connection...
		Connection test = getConnection();
		if (test != null)
		{
			Log.info("SQLDatabaseConnectionManager successfully created a test connection!");
			
			// Start the monitor
			this.startMonitor();
			
			return true;
		}
		else
		{
			Log.error("SQLDatabaseConnectionManager failed to create a test connection, please verify the server details etc!");
			return false;
		}
	}
	
	public void startMonitor()
	{
		if (m_monitorThread == null)
		{
			m_monitorThread = new Thread(this, "SQLDatabaseConnectionManager monitor thread");
			m_monitorThread.setPriority(Thread.MIN_PRIORITY);
			m_monitorThread.start();
			
			Log.info("SQLDatabaseConnectionManager connection pool cleanup monitor started.");
		}
		else
		{
			this.stopMonitor();
			this.startMonitor();
		}
	}
	
	public void stopMonitor()
	{
		if (m_monitorThread != null)
		{
			m_monitorThread.interrupt();
			m_monitorThread = null;
			
			Log.info("SQLDatabaseConnectionManager connection pool cleanup monitor stopped.");
		}
	}
	
	/**
	 * Tries to return a java.sql.Connection that is ready for use. Always return this Connection to this manager again, or it will not get recycled!
	 */
	public Connection getConnection()
	{
		Connection conn = null;
		synchronized (m_waitLock)
		{
			// Any available connections?
			if (m_availableConnections.size() > 0)
			{
				conn = m_availableConnections.remove(m_availableConnections.size() - 1);
				if (this.connectionIsUseable(conn) == false) // Is this connection worth anything?
				{
					m_createdConnections--; // Allow creating a new one
					conn = this.getConnection(); // Recursive
				}
			}
			// Can we create new connection?
			else if (m_maxConnections == 0 || m_createdConnections < m_maxConnections)
			{
				conn = this.createConnection();
				if (conn != null)
				{
					m_createdConnections++;
					Log.info("SQLDatabaseConnectionManager created new Connection, active connection amount: " + (m_availableConnections.size() + 1));
				}
			}
			
			// No available connection? (or could not create new one)
			if (conn == null)
			{
				// Let's wait for a returning connection!
				try
				{
					m_waitLock.wait(5 * 1000); // Wait 5 seconds for teh poke
					
					// If execution gets here, there has been no poking
				}
				catch (InterruptedException ex)
				{
					conn = this.getConnection(); // Yay! Let's try to recycle the returned connection!
				}
			}
		}
		
		// No connection available, log it
		if (conn == null)
		{
			Log.error("Could not get a SQLDatabase connection!");
		}
		
		// Return the result
		return conn;
	}
	
	/**
	 * Returns a java.sql.Connection to the pool again, so it can be recycled in other requests.
	 * 
	 * @param conn The java.sql.Connection to return to the pool.
	 */
	public void releaseConnection(Connection conn)
	{
		if (conn != null)
		{
			synchronized (m_waitLock)
			{
				m_availableConnections.add(conn);
				m_waitLock.notifyAll(); // Poke waiting connection getters
			}
		}
	}
	
	/**
	 * Tries to create a new java.sql.Connection to the database endpoint and returns it.
	 * 
	 * @return A new java.sql.Connection that is opened and ready for usage. Can be null if creating failed!
	 */
	private Connection createConnection()
	{
		Connection conn = null;
		
		try
		{
			conn = DriverManager.getConnection(m_epUrl, m_epUsername, m_epPassword);
		}
		catch (SQLException ex)
		{
			Log.error("Failed to create a new SQL database Connection for " + m_epUrl, ex);
		}
		
		return conn;
	}
	
	/**
	 * Checks if a given java.sql.Connection can be reused by another connection request.
	 * 
	 * @param conn The java.sql.Connection to check.
	 * @return True if the connection can be reused, false if it sucks ass.
	 */
	private boolean connectionIsUseable(Connection conn)
	{
		try
		{
			// Other checks here
			return !conn.isClosed();
		}
		catch (SQLException ex)
		{
			
		}
		
		// Bad
		return false;
	}
	
	/**
	 * Runnable. Checks for inactive connections and closes them to keep the SQL server happy.
	 */
	public void run()
	{
		// Load cleanup interval
		final long cleanupInterval = Environment.getPropBox().getInt("db.sql.cleanupinterval", 180000);
		
		// Run all the time
		while (true)
		{
			int closed = 0;
			synchronized (m_waitLock)
			{
				// Close all connections
				for (Connection conn : m_availableConnections)
				{
					try
					{
						conn.close();
						closed++;
					}
					catch (SQLException ex)
					{
						Log.error("Error while closing Connection in SQLDatabaseConnectionManager", ex);
					}
				}
				
				// Clear those dead connections!
				m_availableConnections.clear();
			}
			
			// Happy logging!
			if (closed > 0)
			{
				Log.info("SQLDatabaseConnectionManager performed connection pool cleanup, " + closed + " connections closed.");
			}
			
			// Sleep...
			try
			{
				Thread.sleep(cleanupInterval);
			}
			catch (InterruptedException ex)
			{
				return;
			}
		}
	}
}
