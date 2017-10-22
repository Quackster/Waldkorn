package net.nillus.waldkorn.storage;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Vector;

public class DatabaseConnectionManager implements Runnable
{
	private Object m_waitLock;
	private DatabaseEndpoint m_database;
	private String m_driver;
	private int m_maxConnections;
	private int m_createdConnections;
	
	private Thread m_monitorThread;
	private Vector<Connection> m_availableConnections;
	
	public DatabaseConnectionManager(DatabaseEndpoint databaseEndpoint, String driver, int maxConnections)
	{
		m_waitLock = new Object();
		m_database = databaseEndpoint;
		m_driver = driver;
		m_maxConnections = maxConnections;
		m_availableConnections = new Vector<Connection>(0);
	}
	
	public boolean test()
	{
		// Register connection driver
		String driverClassName = m_driver;
		try
		{
			Driver driver = (Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(driver);
		}
		catch(Exception ex)
		{
			return false;
		}
		
		// Create test connection
		Connection test = this.getConnection();
		if(test != null)
		{
			this.releaseConnection(test);
			return true;
		}
		else
		{
			return false;	
		}
	}
	
	public void startMonitor()
	{
		if (m_monitorThread == null)
		{
			m_monitorThread = new Thread(this, "DatabaseConnectionManager monitor thread");
			m_monitorThread.setPriority(Thread.MIN_PRIORITY);
			m_monitorThread.start();
		}
	}
	
	public void stopMonitor()
	{
		if (m_monitorThread != null)
		{
			m_monitorThread.interrupt();
			m_monitorThread = null;
		}
	}

	public Connection getConnection()
	{
		Connection conn = null;
		synchronized (m_waitLock)
		{
			// Get a ready-to-roll connection if there is, or create a new one if allowed
			if (m_availableConnections.size() > 0)
			{
				// Fix the connection if it's not useable
				conn = m_availableConnections.remove(m_availableConnections.size() - 1);
				try
				{
					if(conn.isClosed())
					{
						m_createdConnections--;
						conn = this.getConnection();
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					conn = this.getConnection();
				}
			}
			else if (m_maxConnections == 0 || m_createdConnections < m_maxConnections)
			{
				try
				{
					conn = DriverManager.getConnection(m_database.url, m_database.username, m_database.password);
					m_createdConnections++;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					return null;
				}
			}
			
			// Can't create more connections at the moment? Wait for a returning connection
			if (conn == null)
			{
				try
				{
					m_waitLock.wait(5 * 1000);
				}
				catch (InterruptedException ex)
				{
					conn = this.getConnection();
				}
			}
		}
		
		// No connection available, log it
		if (conn == null)
		{
			// Bah, no connection, DB server went away?
		}
		return conn;
	}
	
	public void releaseConnection(Connection conn)
	{
		if (conn != null)
		{
			synchronized (m_waitLock)
			{
				m_availableConnections.add(conn);
				m_waitLock.notifyAll();
			}
		}
	}
	
	public void run()
	{
		// Load cleanup interval
		final long cleanupInterval = 180 * 1000;
		
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
					catch (Exception ex)
					{
						
					}
				}
				
				// Clear those dead connections!
				m_availableConnections.clear();
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
