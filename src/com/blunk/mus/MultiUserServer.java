package com.blunk.mus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import com.blunk.Log;

public class MultiUserServer implements Runnable
{
	private int m_port;
	private boolean m_running;
	private Thread m_thread;
	private Vector<MusClient> m_clients;
	private Class<MusMessageHandler> m_msgHandlerClass;
	
	public MultiUserServer(int port)
	{
		m_port = port;
		m_running = false;
		m_thread = new Thread(this, "Macromedia MultiUserServer emulator");
		m_clients = new Vector<MusClient>();
		m_msgHandlerClass = null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean setMessageHandlerClass(String className)
	{
		try
		{
			Class c = Class.forName(className);
			if (c.newInstance() instanceof MusMessageHandler)
			{
				// Valid
				m_msgHandlerClass = c;
				return true;
			}
		}
		catch (ClassNotFoundException ex)
		{
			Log.error("MultiUserServer: MusMessageHandler class \"" + className + "\" not found!");
		}
		catch (Exception ex)
		{
			Log.error("MultiUserServer: error instantiating MusMessageHandler class \"" + className + "\"!", ex);
		}
		
		// Failure
		return false;
	}
	
	public void start()
	{
		if (!m_running)
		{
			// Start thread
			m_running = true;
			m_thread.start();
		}
	}
	
	public void stop()
	{
		if (m_running)
		{
			// Kill active clients
			this.killClients();
			
			// Stop thread
			m_running = false;
			m_thread.interrupt();
		}
	}
	
	public MusClient getClient(int clientID)
	{
		// Lookup client collection
		synchronized (m_clients)
		{
			for (MusClient client : m_clients)
			{
				if (client.getID() == clientID)
				{
					return client;
				}
			}
		}
		
		// Not here!
		return null;
	}
	
	public boolean removeClient(MusClient client)
	{
		Log.info("MultiUserServer: removed client #" + client.getID());
		
		synchronized (m_clients)
		{
			return m_clients.removeElement(client);
		}
	}
	
	public void killClient(int clientID)
	{
		MusClient client = this.getClient(clientID);
		if (client != null)
		{
			client.stop();
		}
	}
	
	public void killClients()
	{
		// Kill all active clients
		synchronized (m_clients)
		{
			for (MusClient client : m_clients)
			{
				// Stop client
				client.stop();
			}
			
			// Wipe the collection
			m_clients.clear();
		}
	}
	
	public boolean killClientOfUser(int userID)
	{
		synchronized (m_clients)
		{
			for (MusClient client : m_clients)
			{
				if (client.getUserID() == userID)
				{
					client.stop();
					return true;
				}
			}
		}
		
		return false;
	}
	
	public MusClient getClientOfUser(int userID)
	{
		synchronized (m_clients)
		{
			for (MusClient client : m_clients)
			{
				if (client.getUserID() == userID)
				{
					return client;
				}
			}
		}
		
		return null;
	}
	
	public Class<MusMessageHandler> getMessageHandlerClass()
	{
		return m_msgHandlerClass;
	}
	
	public Vector<MusClient> getClients()
	{
		return m_clients;
	}
	
	@Override
	public void run()
	{
		// Setup listener
		ServerSocket listener;
		try
		{
			listener = new ServerSocket(m_port);
		}
		catch (IOException ex)
		{
			Log.error("MultiUserServer: error while binding ServerSocket listener to TCP port " + m_port + ". Port probably in use already or outside of TCP port range.");
			this.stop();
			return;
		}
		
		// Keep accepting new connections
		int clientCounter = 0;
		while (m_running)
		{
			try
			{
				// Create new client for socket
				Socket socket = listener.accept();
				MusClient client = new MusClient(++clientCounter, socket, this);
				
				// Register the appropriate message handler
				if (m_msgHandlerClass != null)
				{
					client.setMessageHandler(m_msgHandlerClass.newInstance());
				}
				
				// Create streams, start thread etc
				if (client.start())
				{
					Log.info("MultiUserServer: accepted client #" + client.getID() + " from " + client.getIpAddress() + " [" + client.getHostName() + "]");
					
					// Add the new client to the collection
					synchronized (m_clients)
					{
						m_clients.add(client);
					}
				}
			}
			catch (IOException ex)
			{
				Log.error("MultiUserServer: IO error while accepting new connection request", ex);
			}
			catch (Exception ex)
			{
				Log.error("MultiUserServer: unhandled exception while handling new connection", ex);
			}
		}
		
		// Close listener
		try
		{
			listener.close();
		}
		catch (IOException ex)
		{
			Log.error("MultiUserServer: error while closing ServerSocket listener", ex);
		}
	}
}