package net.nillus.waldkorn.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.sessions.SessionManager;

public class NetworkConnectionListener extends ServerComponent implements Runnable
{
	private final byte m_type;
	private int m_port;
	private boolean m_alive;
	private Thread m_thread;
	private SessionManager m_sessionMgr;

	public NetworkConnectionListener(int port, byte type, SessionManager sessionMgr)
	{
		super(sessionMgr.getServer());
		
		m_port = port;
		m_sessionMgr = sessionMgr;
		m_type = type;
	}
	
	public void start()
	{
		if (!m_alive)
		{
			// Create thread
			m_thread = new Thread(this, this.toString());
			m_thread.setPriority(Thread.MIN_PRIORITY);
			
			// Start thread
			m_alive = true;
			m_thread.start();
		}
	}
	
	public void stop()
	{
		if(m_alive)
		{
			// Interrupt thread
			m_alive = false;
			m_thread.interrupt();
		}
	}
	
	public void run()
	{
		ServerSocket listener;
		try
		{
			listener = new ServerSocket(m_port);
		}
		catch (IOException ex)
		{
			m_server.getLogger().error(this, "could not bind listener socket to local TCP port " + m_port + "! Port/address probably in use.", ex);
			this.stop();
			return;
		}
		
		// Keep accepting connections
		while(m_alive)
		{
			try
			{
				// Accept this connection
				Socket socket = listener.accept();
				NetworkConnection connection = new NetworkConnection(socket, m_server);
				
				// Create appropriate session
				if(m_type == NetworkConnection.MASTER_CONNECTION)
				{
					m_sessionMgr.addMasterConnection(connection);
				}
				else if(m_type == NetworkConnection.SPACE_CONNECTION)
				{
					m_sessionMgr.addSpaceConnection(connection);
				}
			}
			catch (IOException ex)
			{
				m_server.getLogger().error(this, "error accepting new NetworkConnection!", ex);
			}
			catch (Exception ex)
			{
				m_server.getLogger().error(this, "error creating client", ex);
			}
		}
		
		// Close listener
		try
		{
			listener.close();
		}
		catch (IOException ex)
		{
			m_server.getLogger().error(this, "error closing listener socket", ex);
		}
	}
	
	public int getPort()
	{
		return m_port;
	}

	public String toString()
	{
		return this.getClass().getSimpleName() + " [@ port " + m_port + "]";
	}
}
