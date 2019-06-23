package net.nillus.waldkorn.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import net.nillus.waldkorn.MasterServer;
import net.nillus.waldkorn.ServerComponent;

/**
 * UserRegister provides methods for managing stored users etc.
 * 
 * @author Nillus
 */
public class UserRegister extends ServerComponent
{
	public UserRegister(MasterServer server)
	{
		super(server);
	}
	
	public boolean approveName(String name)
	{
		// Verify length
		if(name.length() < 3 || name.length() > 20)
		{
			return false;
		}
		
		// Check each character in the name, break on illegal character
		final String allowed = getServer().getProperties().get("user.name.chars", "");
		char[] nameChars = name.toCharArray();
		for (int i = 0; i < nameChars.length; i++)
		{
			if (allowed.indexOf(Character.toLowerCase(nameChars[i])) == -1)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean registerUser(User usr, String ip)
	{
		// Valid name?
		if(!this.approveName(usr.name))
		{
			return false;
		}
		
		// User doesn't exist already?
		if(this.getUserInfo(usr.name) != null)
		{
			return false;
		}
		
		// Log what's going on
		getServer().getLogger().info("UserRegister", "registering new User: " + usr.name);
		
		// Set default values
		usr.role = 1;
		usr.credits = getServer().getProperties().getInt("user.default.credits", 0);
		usr.gameTickets = getServer().getProperties().getInt("user.default.gametickets", 0);
		usr.messengerMotto = getServer().getProperties().get("user.default.messengermotto", "");
		usr.poolFigure = "";
		usr.updateLastActivity();
		
		// Insert into database
		PreparedStatement query = null;
		try
		{
			query = m_server.getDatabase().prepareStatement("INSERT INTO users(name,password,email,dob,phonenumber,registered,role,motto,figure,sex,poolfigure,credits,gametickets,motto_messenger,lastactivity) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
			query.setString(1, usr.name);
			query.setString(2, usr.password);
			query.setString(3, usr.email);
			query.setString(4, usr.dateOfBirth);
			query.setString(5, usr.phoneNumber);
			query.setTimestamp(6, new Timestamp(usr.registered.getTime()));
			query.setByte(7, usr.role);
			query.setString(8, usr.motto);
			query.setString(9, usr.figure);
			query.setString(10, Character.toString(usr.sex));
			query.setString(11, usr.poolFigure);
			query.setInt(12, usr.credits);
			query.setInt(13, usr.gameTickets);
			query.setString(14, usr.messengerMotto);
			query.setTimestamp(15, new Timestamp(usr.registered.getTime()));
			return (query.executeUpdate() > 0);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(query);
		}
		
		return false;
	}
	
	public User getUserInfo(int userID)
	{
		ResultSet result = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT * FROM users WHERE id = " + userID + ";");
			if(result.next())
			{
				User user = User.parse(result);
				user.determineBadge();
				return user;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		
		return null;
	}
	
	public User getUserInfo(String name)
	{
		PreparedStatement query = null;
		try
		{
			query = m_server.getDatabase().prepareStatement("SELECT * FROM users WHERE name = ?;");
			query.setString(1, name);
			ResultSet result = query.executeQuery();
			if(result.next())
			{
				User user = User.parse(result);
				user.determineBadge();
				return user;
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
		
		return null;
	}
	
	public int getUserID(String name)
	{
		PreparedStatement query = null;
		try
		{
			query = m_server.getDatabase().prepareStatement("SELECT id FROM users WHERE name = ?;");
			query.setString(1, name);
			ResultSet result = query.executeQuery();
			if(result.next())
			{
				return result.getInt(1);
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
		
		return -1;
	}
}