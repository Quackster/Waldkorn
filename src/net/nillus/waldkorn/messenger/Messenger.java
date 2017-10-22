package net.nillus.waldkorn.messenger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.MasterSession;
import net.nillus.waldkorn.users.User;

public class Messenger
{
	private MasterSession m_session;
	private HashMap<Integer, MessengerBuddy> m_buddies;
	private HashMap<String, Integer> m_buddyRequests;
	
	public Messenger(MasterSession session)
	{
		m_session = session;
		m_buddies = new HashMap<Integer, MessengerBuddy>();
		m_buddyRequests = new HashMap<String, Integer>();
	}
	
	/* BUDDY LIST */
	public void loadBuddies()
	{
		// Clear current list
		m_buddies.clear();
		
		// Load new list from database
		ResultSet rows = null;
		try
		{
			rows = m_session.getServer().getDatabase().executeQuery(
					"SELECT u.id AS id,u.name AS name,u.motto_messenger AS motto_messenger,u.lastactivity AS lastactivity FROM messenger_buddylist INNER JOIN users AS u ON u.id = id1 WHERE id2 = " + m_session.getUserObject().ID
							+ " AND accepted = 1 UNION ALL SELECT u.id AS id,u.name AS name,u.motto_messenger AS motto_messenger,u.lastactivity AS lastactivity FROM messenger_buddylist INNER JOIN users AS u ON u.id = id2 WHERE id1 = "
							+ m_session.getUserObject().ID + " AND accepted = 1;");
			while (rows.next())
			{
				MessengerBuddy buddy = new MessengerBuddy();
				buddy.ID = rows.getInt("id");
				buddy.name = rows.getString("name");
				buddy.messengerMotto = rows.getString("motto_messenger");
				buddy.lastActivity = new Date(rows.getTimestamp("lastactivity").getTime());
				buddy.location = "offline";
				m_buddies.put(buddy.ID, buddy);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_session.getServer().getDatabase().releaseStatement(rows);
		}
	}
	
	public void sendBuddyList()
	{
		ServerMessage msg = new ServerMessage("BUDDYLIST");
		for (MessengerBuddy buddy : m_buddies.values())
		{
			msg.appendObject(buddy);
		}
		m_session.send(msg);
	}
	
	public void refreshBuddyList()
	{
		this.loadBuddies();
		this.sendBuddyList();
	}
	
	public void removeBuddy(int userID)
	{
		// Remove from index
		m_buddies.remove(userID);
		
		// Re-send list
		this.sendBuddyList();
	}
	
	public void requestBuddyRemoval(String name)
	{
		MessengerBuddy buddy = this.getBuddy(name);
		if (buddy != null)
		{
			// Delete from database
			int myUserID = m_session.getUserObject().ID;
			try
			{
				m_session.getServer().getDatabase().executeUpdate("DELETE FROM messenger_buddylist WHERE ((id1 = " + myUserID + " AND id2 = " + buddy.ID + ") OR (id1 = " + buddy.ID + " AND id2 = " + myUserID + ")) AND accepted = 1;");
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			// Remove from buddylists
			this.removeBuddy(buddy.ID);
			
			// Remove from other user if online
			MasterSession other = m_session.getServer().getSessionMgr().getMasterSessionOfUser(buddy.ID);
			if (other != null)
			{
				other.getMessenger().removeBuddy(m_session.getUserObject().ID);
			}
		}
	}
	
	/* BUDDY LIST */

	/* BUDDY REQUESTS */
	public void addBuddyRequest(Integer userID, String name)
	{
		// Add to index
		m_buddyRequests.put(name, userID);
		
		// Send to client
		ServerMessage msg = new ServerMessage("BUDDYADDREQUESTS");
		msg.append("\r");
		msg.appendPartArgument(name);
		m_session.send(msg);
	}
	
	public void loadBuddyRequests()
	{
		// Clear current index
		m_buddyRequests.clear();
		
		// Load new ones from database, send them to client and add them to the index
		ResultSet rows = null;
		try
		{
			rows = m_session.getServer().getDatabase().executeQuery("SELECT id,name FROM users WHERE id IN(SELECT id1 FROM messenger_buddylist WHERE id2 = " + m_session.getUserObject().ID + " AND accepted = 0);");
			while (rows.next())
			{
				Integer id = rows.getInt("id");
				String name = rows.getString("name");
				this.addBuddyRequest(id, name);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_session.getServer().getDatabase().releaseStatement(rows);
		}
	}
	
	public void createBuddyRequest(String name)
	{
		// Target exists, and not buddy yet?
		User target = m_session.getServer().getUserRegister().getUserInfo(name);
		if (target == null || target.ID == m_session.getUserObject().ID || this.hasBuddy(target.ID))
		{
			return;
		}
		
		// Prevent staff spam etc
		if (target.role > m_session.getUserObject().role)
		{
			return;
		}
		
		// Write to database
		try
		{
			m_session.getServer().getDatabase().executeUpdate("INSERT INTO messenger_buddylist(id1,id2,accepted) VALUES (" + m_session.getUserObject().ID + "," + target.ID + ",0);");
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			return;
		}
		
		// Notify other end if online
		MasterSession targetSession = m_session.getServer().getSessionMgr().getMasterSessionOfUser(target.ID);
		if (targetSession != null)
		{
			targetSession.getMessenger().addBuddyRequest(m_session.getUserObject().ID, m_session.getUserObject().name);
		}
	}
	
	public void acceptBuddyRequest(String name)
	{
		int otherID = this.getBuddyRequest(name);
		if (otherID != -1)
		{
			// Update the database
			try
			{
				m_session.getServer().getDatabase().executeUpdate("UPDATE messenger_buddylist SET accepted = 1 WHERE id1 = " + otherID + " AND id2 = " + m_session.getUserObject().ID + ";");
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			// Refresh own list
			this.refreshBuddyList();
			
			// Notify other end if online
			MasterSession otherSession = m_session.getServer().getSessionMgr().getMasterSessionOfUser(otherID);
			if (otherSession != null)
			{
				otherSession.getMessenger().refreshBuddyList();
			}
		}
	}
	
	public void declineBuddyRequest(String name)
	{
		int otherID = this.getBuddyRequest(name);
		if (otherID != -1)
		{
			// Update the database
			try
			{
				m_session.getServer().getDatabase().executeUpdate("DELETE FROM messenger_buddylist WHERE id1 = " + otherID + " AND id2 = " + m_session.getUserObject().ID + " AND accepted = 0;");
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			// Remove from index
			m_buddyRequests.remove(name);
		}
	}
	
	/* BUDDY REQUESTS */

	/* MESSAGES */
	public void addMessage(MessengerMessage msg)
	{
		ServerMessage smsg = new ServerMessage("MESSENGER_MSG");
		smsg.appendObject(msg);
		m_session.send(smsg);
	}
	
	public void sendUnreadMessages()
	{
		ResultSet rows = null;
		try
		{
			rows = m_session.getServer().getDatabase().executeQuery(
					"SELECT m.id,m.senderid,m.timestamp,m.text,users.figure FROM messenger_messages AS m INNER JOIN users ON m.senderid = users.id WHERE receiverid = " + m_session.getUserObject().ID + " AND isread = 0;");
			while (rows.next())
			{
				MessengerMessage msg = MessengerMessage.parse(rows);
				this.addMessage(msg);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_session.getServer().getDatabase().releaseStatement(rows);
		}
	}
	
	public void sendMessage(int[] receiverIDs, String text)
	{
		// Wrap up all the data in a message object
		MessengerMessage msg = new MessengerMessage();
		msg.text = text;
		msg.timestamp = new Date();
		msg.senderID = m_session.getUserObject().ID;
		msg.senderFigure = m_session.getUserObject().figure;
		
		// Use a PreparedStatement
		PreparedStatement query = null;
		try
		{
			query = m_session.getServer().getDatabase().prepareStatement("INSERT INTO messenger_messages(senderid,receiverid,timestamp,text,isread) VALUES (?,?,?,?,0);");
			
			// Start delivering
			for (int receiverID : receiverIDs)
			{
				// Only deliver to our buddies
				if (this.hasBuddy(receiverID))
				{
					// Insert in database
					query.setInt(1, msg.senderID);
					query.setInt(2, receiverID);
					query.setTimestamp(3, new Timestamp(msg.timestamp.getTime()));
					query.setString(4, msg.text);
					query.executeUpdate();
					
					// Get generated keys
					ResultSet keys = query.getGeneratedKeys();
					msg.ID = keys.next() ? keys.getInt(1) : -1;
					
					// Receiver online?
					MasterSession receiver = m_session.getServer().getSessionMgr().getMasterSessionOfUser(receiverID);
					if (receiver != null)
					{
						receiver.getMessenger().addMessage(msg);
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_session.getServer().getDatabase().releaseStatement(query);
		}
	}
	
	public void markMessageAsRead(int messageID)
	{
		try
		{
			m_session.getServer().getDatabase().executeUpdate("UPDATE messenger_messages SET isread = 1 WHERE id = " + messageID + " AND receiverid = " + m_session.getUserObject().ID + " LIMIT 1;");
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	/* MESSAGES */

	/* BUDDY & REQUEST MANAGEMENT */
	public boolean hasBuddy(int userID)
	{
		return m_buddies.containsKey(userID);
	}
	
	public MessengerBuddy getBuddy(int userID)
	{
		return m_buddies.get(userID);
	}
	
	public boolean hasBuddy(String name)
	{
		return (this.getBuddy(name) != null);
	}
	
	public MessengerBuddy getBuddy(String name)
	{
		for (MessengerBuddy buddy : m_buddies.values())
		{
			if (buddy.name.equals(name))
			{
				return buddy;
			}
		}
		return null;
	}
	
	public int getBuddyRequest(String name)
	{
		return m_buddyRequests.get(name);
	}
	
	/* BUDDY & REQUEST MANAGEMENT */

	/* MISC */
	public MasterSession getSession()
	{
		return m_session;
	}
	/* MISC */
}
