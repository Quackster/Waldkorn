package com.blunk.mus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.blunk.Log;
import com.blunk.util.TimeHelper;

public class MusClient implements Runnable
{
	private int m_ID;
	private MultiUserServer m_server;
	
	private Socket m_socket;
	private Thread m_thread;
	private boolean m_alive;
	
	private DataInputStream m_in;
	private DataOutputStream m_out;
	
	private int m_userID;
	private MusMessageHandler m_msgHandler;
	
	public MusClient(int ID, Socket socket, MultiUserServer server)
	{
		m_ID = ID;
		m_socket = socket;
		m_server = server;
		
		// Create thread with lowest priority
		m_thread = new Thread(this, "MusClient[" + m_ID + "]");
		m_thread.setPriority(Thread.MIN_PRIORITY);
	}
	
	public void setMessageHandler(MusMessageHandler handler)
	{
		m_msgHandler = handler;
	}
	
	public boolean start()
	{
		if (!m_alive)
		{
			// Create the socket I/O streams
			try
			{
				m_in = new DataInputStream(m_socket.getInputStream());
				m_out = new DataOutputStream(m_socket.getOutputStream());
			}
			catch (IOException ex)
			{
				Log.error("MusClient " + m_ID + ": error creating I/O streams on socket", ex);
				return false;
			}
			
			// Start the new thread
			m_alive = true;
			m_thread.start();
			return true;
		}
		
		return false;
	}
	
	public void stop()
	{
		// Stop the thread
		m_alive = false;
		m_thread.interrupt();
		
		// Close the I/O streams (if constructed successfully)
		if (m_in != null)
		{
			try
			{
				m_in.close();
				m_out.close();
			}
			catch (IOException ex)
			{
				Log.error("MusClient " + m_ID + ": error closing I/O streams on socket", ex);
			}
		}
		
		// Close the socket
		try
		{
			m_socket.close();
		}
		catch (Exception ex)
		{
			// Oh well!
			ex.printStackTrace();
		}
		
		// Destroy resources in the message handler
		if (m_msgHandler != null)
		{
			m_msgHandler.cleanup();
		}
		
		// Remove from server?
		m_server.removeClient(this);
	}
	
	public void run()
	{
		while (m_alive)
		{
			try
			{
				// Read & validate header tag
				byte headerTag = m_in.readByte();
				m_in.readByte();
				if (headerTag != 'r')
				{
					// Bad header tag, or no more input on the stream
					m_alive = false;
				}
				else
				{
					// Start parsing the incoming message
					MusMessage msg = new MusMessage();
					
					// Message length
					msg.size = m_in.readInt();
					
					// We are going to read all the body data in a byte array, so we know that we have received all data
					byte[] msgBody = new byte[msg.size];
					int read = 0;
					while (read < msg.size)
					{
						read += m_in.read(msgBody, read, msg.size - read);
					}
					
					// Create a reader to read from the byte array (supports reading primitive types by default)
					DataInputStream reader = new DataInputStream(new ByteArrayInputStream(msgBody));
					
					// Error code
					msg.errorCode = reader.readInt();
					
					// Timestamp
					msg.timeStamp = reader.readInt();
					
					// Subject
					msg.subject = MusUtil.readEvenPaddedString(reader);
					
					// Sender
					msg.senderID = MusUtil.readEvenPaddedString(reader);
					
					// Receivers
					msg.receivers = new String[reader.readInt()];
					for (int i = 0; i < msg.receivers.length; i++)
					{
						msg.receivers[i] = MusUtil.readEvenPaddedString(reader);
					}
					
					// Content
					// EXCEPTION: "Logon", breaks standard element formatting
					if (msg.subject.equals("Logon"))
					{
						// Read in remaining data
						byte[] tmpBytes = new byte[reader.available()];
						reader.read(tmpBytes);
						
						// Set fields
						msg.contentType = MusTypes.String;
						msg.contentString = new String(tmpBytes);
					}
					else
					{
						// Content type
						msg.contentType = reader.readShort();
						
						// Read in content
						if (msg.contentType == MusTypes.Integer)
						{
							msg.contentInt = reader.readInt();
						}
						else if (msg.contentType == MusTypes.String)
						{
							msg.contentString = MusUtil.readEvenPaddedString(reader);
						}
						else if (msg.contentType == MusTypes.PropList)
						{
							msg.contentPropList = MusUtil.readPropList(reader);
						}
					}
					
					// Message parsed, now handle it
					Log.debug(this + " <-- " + msg.toString());
					if (m_msgHandler != null)
					{
						m_msgHandler.handleIncomingMessage(this, msg);
					}
				}
			}
			catch (EOFException ex)
			{
				// Just a disconnect, no real error
				m_alive = false;
			}
			catch (SocketException ex)
			{
				// Socket closed by remote end
				m_alive = false;
			}
			catch (Exception ex)
			{
				// What happened?
				Log.error("MusClient: abnormal error while receiving/parsing data!", ex);
				m_alive = false;
			}
		}
		
		// Salaam!
		this.stop();
	}
	
	public boolean sendMessage(MusMessage msg)
	{
		if (msg != null)
		{
			// Log it
			Log.debug(this + " --> " + msg.toString());
			
			// Sign message by system
			msg.senderID = "System";
			msg.receivers = new String[] { "*" };
			msg.timeStamp = (int)TimeHelper.getTime();
			
			try
			{
				// Temporarily buffer for all data past message length
				ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
				DataOutputStream buffer = new DataOutputStream(bufferStream);
				
				// Error code
				buffer.writeInt(msg.errorCode);
				
				// Timestamp
				buffer.writeInt(msg.timeStamp);
				
				// Subject
				MusUtil.writeEvenPaddedString(buffer, msg.subject);
				
				// Sender
				MusUtil.writeEvenPaddedString(buffer, msg.senderID);
				
				// Receivers
				buffer.writeInt(msg.receivers.length);
				for (int i = 0; i < msg.receivers.length; i++)
				{
					MusUtil.writeEvenPaddedString(buffer, msg.receivers[i]);
				}
				
				// Content type
				buffer.writeShort(msg.contentType);
				
				// Content
				if (msg.contentType != MusTypes.Void)
				{
					if (msg.contentType == MusTypes.Integer)
					{
						buffer.writeInt(msg.contentInt);
					}
					else if (msg.contentType == MusTypes.String)
					{
						MusUtil.writeEvenPaddedString(buffer, msg.contentString);
					}
					else if (msg.contentType == MusTypes.PropList)
					{
						MusUtil.writePropList(buffer, msg.contentPropList);
					}
					else
					{
						System.out.println("Unsupported MusMessage content type " + msg.contentType + "!");
					}
				}
				
				// Flush buffer to ensure all data is in it
				buffer.flush();
				bufferStream.flush();
				
				// Extract buffer to a byte array
				byte[] body = bufferStream.toByteArray();
				buffer.close();
				bufferStream.close();
				bufferStream = null;
				buffer = null;
				
				// Start writing the full message to the socket output
				// Header tag
				m_out.writeByte('r');
				m_out.writeByte(0);
				
				// Length of body
				m_out.writeInt(body.length);
				
				// Body
				m_out.write(body);
				
				// Flush the stream
				m_out.flush();
				
				// Sent!
				return true;
			}
			catch (Exception ex)
			{
				Log.error("MusClient " + m_ID + ": error while sending MusMessage", ex);
			}
		}
		
		// Sending failed
		return false;
	}
	
	public int getID()
	{
		return m_ID;
	}
	
	public String getIpAddress()
	{
		return m_socket.getInetAddress().getHostAddress();
	}
	
	public String getHostName()
	{
		return m_socket.getInetAddress().getHostName();
	}
	
	/**
	 * Sets the ID of the MUS user populating this MusClient.
	 * 
	 * @param ID The ID of the MUS user
	 */
	public void setUserID(int ID)
	{
		m_userID = ID;
	}
	
	/**
	 * Returns the ID (number) of the MUS user populating this MusClient.
	 */
	public int getUserID()
	{
		return m_userID;
	}
	
	public MusMessageHandler getMessageHandler()
	{
		return m_msgHandler;
	}
	
	public MultiUserServer getServer()
	{
		return m_server;
	}
	
	public String toString()
	{
		return "MusClient [" + m_ID + "][]";
	}
}
