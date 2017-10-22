package net.nillus.waldkorn.net;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import net.nillus.waldkorn.Server;
import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.sessions.Session;

public final class NetworkConnection extends ServerComponent implements Runnable
{
	// Constants
	public final static byte MASTER_CONNECTION = 0;
	public final static byte SPACE_CONNECTION = 1;
	
	// Connection
	private Socket m_socket;
	private Thread m_thread;
	private boolean m_alive;
	private DataInputStream m_in;
	private PrintWriter m_out;
	
	// Session reference
	private Session m_session;
	
	public NetworkConnection(Socket socket, Server server) throws Exception
	{
		super(server);
		m_socket = socket;
		
		// Create streams
		m_in = new DataInputStream(m_socket.getInputStream());
		m_out = new PrintWriter(m_socket.getOutputStream());
		
		// Create thread
		m_alive = true;
		m_thread = new Thread(this, "NetworkConnection");
		m_thread.setPriority(Thread.MIN_PRIORITY);
	}
	
	public void startSession(Session session)
	{
		m_session = session;
		session.setConnection(this);
		
		// Start the connection thread
		m_thread.start();
	}
	
	public void disconnect()
	{
		// Already done?
		if (!m_alive)
		{
			return;
		}
		
		m_alive = false;
		m_thread.interrupt();
		try
		{
			m_socket.close();
		}
		catch (Exception ex)
		{
			
		}
		try
		{
			m_in.close();
			m_out.close();
		}
		catch (Exception ex)
		{
			
		}
	}
	
	public void send(char[] data, int offset, int length)
	{
		try
		{
			m_out.write(data, offset, length);
			m_out.flush();
		}
		catch (Exception ex)
		{
			m_session.requestRemoval("send error");
		}
	}
	
	public void send(String str)
	{
		try
		{
			m_out.write(str);
			m_out.flush();
		}
		catch (Exception ex)
		{
			m_session.requestRemoval("send error");
		}
	}
	
	public void send(ServerMessage msg)
	{
		this.send(msg.getResult());
	}
	
	public void run()
	{
		while (m_alive)
		{
			try
			{
				// Read in message length
				String header = "";
				for (int i = 0; i < 4; i++)
				{
					char x = (char)m_in.read();
					if (Character.isDigit(x))
					{
						header += x;
					}
				}
				
				// Read in message payload
				int msgLength = Integer.parseInt(header);
				if (msgLength <= 0)
				{
					m_alive = false;
				}
				else
				{
					char[] msgData = new char[msgLength];
					for (int i = 0; i < msgLength; i++)
					{
						msgData[i] = (char)m_in.read();
					}
					
					// Filter out '#', it's EOF for server>client messages (security issue)
					for (int i = 0; i < msgData.length; i++)
					{
						if (msgData[i] == '#')
						{
							msgData[i] = '?';
						}
					}
					
					// Try to parse ClientMessage object
					ClientMessage msg = ClientMessage.parse(msgData);
					if (msg != null)
					{
						m_session.handleMessage(msg);
					}
				}
			}
			catch (NumberFormatException ex)
			{
				m_alive = false;
			}
			catch (EOFException ex)
			{
				// No more data to read, end of stream = end of connection
				m_alive = false;
			}
			catch (SocketException ex)
			{
				// Remote end closed connection, this usually happens when; client A on machine X is logged in to account 1, client B on machine X logs in to account 1
				m_alive = false;
			}
			catch (Exception ex)
			{
				//m_client.getServer().getLogger().error("NetworkConnection #" + m_client.sessionID + ": abnormal error!", ex);
			}
		}
		
		// Connection is no longer alive, notify session
		m_session.requestRemoval("disconnected by user");
	}
	
	public String getIpAddress()
	{
		return m_socket.getInetAddress().getHostAddress();
	}
	
	public String getHostName()
	{
		return m_socket.getInetAddress().getHostName();
	}
	
	public int getLocalPort()
	{
		return m_socket.getLocalPort();
	}
	
	public Session getSession()
	{
		return m_session;
	}
}
