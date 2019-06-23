package com.suelake.habbo.spaces.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import com.blunk.storage.sql.SQLDataQuery;
import com.suelake.habbo.spaces.FavoriteFlatListHelper;

public class SQLFavoriteFlatListHelper extends FavoriteFlatListHelper implements SQLDataQuery
{
	@Override
	public void execute(Connection conn) throws SQLException
	{
		// Create the statement
		PreparedStatement query;
		
		// Add or remove this entry?
		if (super.addFavorite)
		{
			query = conn.prepareStatement("INSERT INTO users_favoriteflats(userid,spaceid) VALUES (?,?);");
		}
		else
		{
			query = conn.prepareStatement("DELETE FROM users_favoriteflats WHERE userid = ? AND spaceid = ? LIMIT 1;");
		}
		
		// Set parameters
		query.setInt(1, super.userID);
		query.setInt(2, super.spaceID);
		
		// Execute & close
		query.executeUpdate();
		query.close();
	}
	
	@Override
	public Vector<Integer> query(Connection conn) throws SQLException
	{
		return null;
	}
}
