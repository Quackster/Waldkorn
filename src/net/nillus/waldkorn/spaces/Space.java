package net.nillus.waldkorn.spaces;


import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.storage.Database;

/**
 * Space is the information on a public space or user flat, such as details of the space etc.
 * 
 * @author Nillus
 */
public class Space implements SerializableObject, Comparable<Space>
{
	/**
	 * The database ID of this space. Unique.
	 */
	public int ID;
	/**
	 * The database ID of the User that owns this space.
	 */
	public int ownerID;
	
	/**
	 * The name of this space, displayed to users of the service.
	 */
	public String name;
	/**
	 * The description of this space. If this space is a userflat, it holds a small description on
	 * this user flat. If this space is a public space, it holds the name of the castfile.
	 */
	public String description;
	/**
	 * The model type of this space, each model has it's own heightmap, door position etc.
	 */
	public String model;
	/**
	 * The current amount of Users in this space.
	 */
	public short usersNow;
	/**
	 * The maximum amount of simultaneous Users this space can hold. Some Users can override this
	 * limit.
	 */
	public short usersMax;
	
	/**
	 * User flat only. The name of the User that owns this space.
	 */
	public String owner;
	/**
	 * User flat only. If false, only Users with special privileges can see the name of the User
	 * that owns this user flat.
	 */
	public boolean showOwner;
	/**
	 * User flat only. If true, all Users inside have the right to drop items and to move items
	 * around.
	 */
	public boolean superUsers;
	public String accessType;
	/***
	 * User flat only. If accessType = 'password', the password that Users must give when entering
	 * this user flat is stored in this field.
	 */
	public String password;
	/**
	 * User flat only. The wallpaper FLATPROPERTY of this user flat.
	 */
	public short wallpaper;
	/**
	 * User flat only. The floor FLATPROPERTY of this user flat.
	 */
	public short floor;
	
	/**
	 * The TCP portnumber to run this Space on. Allocated by SpaceServer.
	 */
	public int port;
	
	/**
	 * The hostname/IP address of the server of this flat. (PublicSpace server etc)
	 */
	public static String host;
	/**
	 * The portnumber of the flatserver to use.
	 */
	public static int flatPort;
	
	public boolean isUserFlat()
	{
		return (this.ownerID > 0);
	}
	
	public static Space parse(ResultSet row) throws Exception
	{
		Space space = new Space();
		space.ID = row.getInt("id");
		space.ownerID = row.getInt("ownerid");
		space.name = row.getString("name");
		space.description = row.getString("description");
		space.model = row.getString("model");
		space.usersNow = row.getShort("users_now");
		space.usersMax = row.getShort("users_max");
		space.showOwner = row.getBoolean("showowner");
		space.superUsers = row.getBoolean("superusers");
		space.accessType = row.getString("accesstype");
		
		if(space.accessType == "")
		{
			space.accessType = "open";
		}
		
		space.wallpaper = row.getShort("wallpaper");
		space.floor = row.getShort("floor");
		space.password = row.getString("password");
		if (space.ownerID > 0)
		{
			space.owner = row.getString("owner");
		}
		return space;
	}
	
	public void update(Database db)
	{
		PreparedStatement query = null;
		try
		{
			query = db.prepareStatement("UPDATE spaces SET name = ?,description = ?,accesstype = ?,password = ?,showowner = ?,superusers = ?,wallpaper = ?,floor = ?,users_now = ? WHERE id = ?;");
			query.setString(1, this.name);
			query.setString(2, this.description);
			if(this.accessType == "")
			{
				query.setString(3, "open");
			}
			else
			{
				query.setString(3, this.accessType);
			}
			query.setString(4, this.password);
			query.setBoolean(5, this.showOwner);
			query.setBoolean(6, this.superUsers);
			query.setShort(7, this.wallpaper);
			query.setShort(8, this.floor);
			query.setShort(9, this.usersNow);
			query.setInt(10, this.ID);
			query.executeUpdate();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			db.releaseStatement(query);
		}
	}
	@Override
	public void serialize(ServerMessage msg)
	{
		if(this.isUserFlat())
		{
			msg.appendNewArgument(Integer.toString(this.ID));
			msg.appendPartArgument(this.name);
			msg.appendPartArgument(this.owner);
			msg.appendPartArgument(this.accessType);
			msg.appendPartArgument("");
			msg.appendPartArgument("waldkorn");
			msg.appendPartArgument(Space.host); // Flat server host
			msg.appendPartArgument(Space.host); // Flat server host 2
			msg.appendPartArgument(Integer.toString(Space.flatPort));
			msg.appendPartArgument(Short.toString(this.usersNow));
			msg.appendPartArgument("null");
			msg.appendPartArgument(this.description);
		}
		else
		{
			msg.appendNewArgument(this.name);
			msg.appendArgument(Short.toString(this.usersNow), ',');
			msg.appendArgument(Short.toString(this.usersMax), ',');
			msg.appendArgument(Space.host, ',');
			msg.appendArgument(Space.host, '/');
			msg.appendArgument(Integer.toString(this.port), ',');
			msg.appendArgument(this.name, ',');
			msg.appendArgument(this.description, ',');
			msg.appendArgument(Short.toString(this.usersNow), ',');
			msg.appendArgument(Short.toString(this.usersMax), ',');
			msg.appendArgument(this.model, ',');
		}
	}

	@Override
	public int compareTo(Space other)
	{
		if(this.usersNow > other.usersNow)
		{
			return -1;
		}
		else if(this.usersNow < other.usersNow)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
