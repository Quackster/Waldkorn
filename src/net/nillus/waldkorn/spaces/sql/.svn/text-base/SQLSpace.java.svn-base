package com.suelake.habbo.spaces.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.blunk.Log;
import com.blunk.storage.sql.SQLDataObject;
import com.suelake.habbo.spaces.Space;

public class SQLSpace extends Space implements SQLDataObject
{
	@Override
	public boolean delete(Connection conn) throws SQLException
	{
		// Prepare the query
		PreparedStatement query = (PreparedStatement)conn.prepareStatement("DELETE FROM spaces WHERE id = ?;");
		query.setInt(1, super.ID);
		
		// And execute
		boolean result = (query.executeUpdate() > 0);
		query.close();
		
		return (result);
	}
	
	private void setParams(PreparedStatement query) throws SQLException
	{
		query.setInt(1, super.ownerID);
		query.setString(2, super.name);
		query.setString(3, super.description);
		query.setString(4, super.model);
		query.setShort(5, super.usersNow);
		query.setShort(6, super.usersMax);
		query.setBoolean(7, super.showOwner);
		query.setBoolean(8, super.superUsers);
		query.setString(9, super.accessType);
		query.setString(10, super.password);
		query.setShort(11, super.wallpaper);
		query.setShort(12, super.floor);
	}
	
	@Override
	public boolean insert(Connection conn) throws SQLException
	{
		// Prepare the query
		PreparedStatement query = conn.prepareStatement("INSERT INTO spaces(ownerid,name,description,model,users_now,users_max,showowner,superusers,accesstype,password,wallpaper,floor) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);",
				PreparedStatement.RETURN_GENERATED_KEYS);
		this.setParams(query);
		
		// Execute
		query.executeUpdate();
		
		// And get the latest inserted ID
		ResultSet keys = query.getGeneratedKeys();
		
		if(keys.next())
		{
			super.ID = keys.getInt(1);
			query.close();
			
			return true;
		}
		
		query.close();
		
		// Insertion failed!
		return false;
	}
	
	@Override
	public boolean update(Connection conn) throws SQLException
	{
		// Prepare the query
		PreparedStatement query = conn.prepareStatement("UPDATE spaces SET ownerid=?,name=?,description=?,model=?,users_now=?,users_max=?,showowner=?,superusers=?,accesstype=?,password=?,wallpaper=?,floor=? WHERE id = ?;");
		this.setParams(query);
		query.setInt(13, super.ID);
		
		// And execute
		boolean result = (query.executeUpdate() > 0);
		query.close();
		
		return (result);
	}
	
	@Override
	public boolean load(Connection conn) throws SQLException
	{
		// Prepare the query
		PreparedStatement query = conn.prepareStatement("SELECT spaces.*,users.name AS owner FROM spaces LEFT JOIN users ON spaces.ownerid = users.id WHERE spaces.id = ?;");
		query.setInt(1, super.ID);
		
		// Execute and try to parse result
		ResultSet result = query.executeQuery();
		
		if (result.next())
		{
			if(SQLSpace.parseFromResultSet(this, result))
			{
				query.close();
				return true;
			}
		}
		
		query.close();
		// Failed to parse or no results
		return false;
	}
	
	public static boolean parseFromResultSet(Space space, ResultSet result)
	{
		try
		{
			space.ID = result.getInt("id");
			space.ownerID = result.getInt("ownerid");
			space.name = result.getString("name");
			space.description = result.getString("description");
			space.model = result.getString("model");
			space.usersNow = result.getShort("users_now");
			space.usersMax = result.getShort("users_max");
			space.showOwner = result.getBoolean("showowner");
			space.superUsers = result.getBoolean("superusers");
			space.accessType = result.getString("accesstype");
			space.wallpaper = result.getShort("wallpaper");
			space.floor = result.getShort("floor");
			space.password = result.getString("password");
			if (space.isUserFlat())
			{
				space.owner = result.getString("owner");
			}
			
			return true;
		}
		catch (Exception ex)
		{
			Log.error("Could not fully parse net.scriptomatic.habbo.spaces.Space (SQLSpace) from given java.sql.ResultSet, probably fields missing or bad data!", ex);
		}
		
		return false;
	}
}
