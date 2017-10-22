package net.nillus.waldkorn.spaces.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import net.nillus.waldkorn.spaces.SpaceContentDeleteHelper;

import com.blunk.storage.sql.SQLDataQuery;

public class SQLSpaceContentDeleteHelper extends SpaceContentDeleteHelper implements SQLDataQuery
{
	@Override
	public void execute(Connection conn) throws SQLException
	{
		// Create the statement
		PreparedStatement query;
		
		// Delete the items?
		if (super.deleteItems)
		{
			query = conn.prepareStatement("DELETE FROM items WHERE spaceid = ?;");
			query.setInt(1, super.spaceID);
			query.executeUpdate();
			query.close();
		}
		
		// Delete flatcontrollers?
		if (super.deleteFlatControllers)
		{
			query = conn.prepareStatement("DELETE FROM spaces_flatcontrollers WHERE spaceid = ?;");
			query.setInt(1, super.spaceID);
			query.executeUpdate();
			query.close();
		}
	}
	
	@Override
	public Vector<Integer> query(Connection conn) throws SQLException
	{
		return null;
	}
}
