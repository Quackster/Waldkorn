package net.nillus.waldkorn.spaces.sql;

import com.blunk.storage.sql.SQLDataQuery;
import net.nillus.waldkorn.spaces.PublicSpaceFinder;
import net.nillus.waldkorn.spaces.Space;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class SQLPublicSpaceFinder implements PublicSpaceFinder, SQLDataQuery
{
	@Override
	public void execute(Connection conn) throws SQLException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Vector<Space> query(Connection conn) throws SQLException
	{
		// Execute query
		ResultSet result = conn.createStatement().executeQuery("SELECT * FROM spaces WHERE ownerid = 0 ORDER BY id;");
		
		// Fetch results
		Vector<Space> found = new Vector<Space>(5);
		while (result.next())
		{
			Space space = new SQLSpace();
			if(SQLSpace.parseFromResultSet(space, result))
			{
				found.add(space);
			}
		}
		
		result.close();
		
		// Return the results
		return found;
	}
}
