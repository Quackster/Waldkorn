package net.nillus.waldkorn.access;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import net.nillus.waldkorn.Server;
import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.users.UserRole;

public class AccessControl extends ServerComponent
{
	private String m_messageOfTheDay;
	
	public AccessControl(Server server)
	{
		super(server);
		
		m_messageOfTheDay = null;
	}
	
	public void loadUserRoleRights()
	{
		PreparedStatement query = null;
		Vector<String> tmp = new Vector<String>();
		try
		{
			query = m_server.getDatabase().prepareStatement("SELECT userright FROM users_rights WHERE role = ?;");
			for (byte roleID = 0; roleID <= UserRole.MAX_USER_ROLE; roleID++)
			{
				query.setByte(1, roleID);
				ResultSet result = query.executeQuery();
				while(result.next())
				{
					tmp.add(result.getString("userright"));
				}
				
				// Put the temporarily list into a string array for faster lookup
				String[] rights = new String[tmp.size()];
				for(int x = 0; x < rights.length; x++)
				{
					rights[x] = tmp.get(x);
				}
				UserRole.setUserRights(roleID, rights);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(query);
		}
		
		m_server.getLogger().info("AccessControl", "loaded user roles and rights");
	}
	
	public void logLogin(UserAccessEntry entry)
	{
		if (entry != null)
		{
			entry.login = new Date();
			logLoginSQL(entry);
		}
	}
	
	public boolean logLoginSQL(UserAccessEntry entry)
	{
		// Fill up fields that would turn out to NULL for now
		PreparedStatement query = null;
		try
		{
			/*
			 * id
			 * userid
			 * ip
			 * login
			 * logout
			 * registration
			 */
			// Insert the item into the database
			query = m_server.getDatabase().prepareStatement("INSERT INTO users_access(userid,ip,login,registration) VALUES (?,?,?,?);");
			query.setInt(1, entry.userID);
			query.setString(2, entry.ip);
			query.setTimestamp(3, new Timestamp(entry.login.getTime()));
			query.setBoolean(4, entry.isRegistration);
			query.executeUpdate();
			
			// Fetch the generated keys
			ResultSet keys = query.getGeneratedKeys();
			if(keys.next())
			{
				entry.ID = keys.getInt(1);
				return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(query);
		}
		
		// Insertion failed!
		return false;
	}
	
	public void logLogout(UserAccessEntry entry)
	{
		if (entry != null && entry.ID > 0)
		{
			entry.logout = new Date();
		}
	}
	
	public UserAccessEntry getLatestAccessEntry(int userID)
	{
		UserAccessEntry entry = new UserAccessEntry();
		
		return entry;
	}
	
	public String getIpIsBanned(String ip)
	{
		return null;
	}
	
	public String getMessageOfTheDay()
	{
		return m_messageOfTheDay;
	}
	
	public void setMessageOfTheDay(String motd)
	{
		m_messageOfTheDay = motd;
	}

	public UserAccessEntry newUserAccessEntry()
	{
		UserAccessEntry entry = new UserAccessEntry();
		return entry;
	}
}
