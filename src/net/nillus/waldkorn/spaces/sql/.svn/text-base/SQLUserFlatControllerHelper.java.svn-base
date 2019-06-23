package com.suelake.habbo.spaces.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.blunk.storage.sql.SQLDataQuery;
import com.suelake.habbo.spaces.UserFlatControllerHelper;

public class SQLUserFlatControllerHelper extends UserFlatControllerHelper implements SQLDataQuery
{
	@Override
	public void execute(Connection conn) throws SQLException
	{
		// Create the query
		PreparedStatement query;
		if(super.addFlatController)
		{
			query = conn.prepareStatement("INSERT INTO spaces_flatcontrollers(spaceid,userid) VALUES (?,?);");
		}
		else
		{
			query = conn.prepareStatement("DELETE FROM spaces_flatcontrollers WHERE spaceid = ? AND userid = ? LIMIT 1;");
		}
		
		// Set the values
		query.setInt(1, super.spaceID);
		query.setInt(2, super.userID);
		
		// Execute the query
		query.executeUpdate();
		query.close();
	}
	
	@Override
	public Vector<Integer> query(Connection conn) throws SQLException
	{
		// Prepare the query
		PreparedStatement query = conn.prepareStatement("SELECT userid FROM spaces_flatcontrollers WHERE spaceid = ?;");
		query.setInt(1, super.spaceID);
		
		// Execute the query
		ResultSet result = query.executeQuery();
		
		// Unwrap and parse the results
		Vector<Integer> found = new Vector<Integer>();
		while (result.next())
		{
			found.add(result.getInt("userid"));
		}
		query.close();
		
		// Return the result bucket
		return found;
	}
}
