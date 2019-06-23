package com.suelake.habbo.spaces.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.blunk.storage.sql.SQLDataQuery;
import com.suelake.habbo.spaces.SpaceBotLoader;
import com.suelake.habbo.spaces.instances.SpaceBot;
import com.suelake.habbo.users.User;

public class SQLSpaceBotLoader extends SpaceBotLoader implements SQLDataQuery
{
	@Override
	public void execute(Connection conn) throws SQLException
	{
		
	}
	
	@Override
	public Vector<?> query(Connection conn) throws SQLException
	{
		// Create query
		PreparedStatement query = conn.prepareStatement("SELECT * FROM spaces_bots WHERE spaceid = ?;");
		query.setInt(1, super.spaceID);
		
		// Execute query
		ResultSet result = query.executeQuery();
		Vector<SpaceBot> bots = new Vector<SpaceBot>(0);
		
		// Parse results
		while(result.next())
		{
			// Parse info
			User info = new User();
			info.ID = result.getInt("id");
			info.name = result.getString("name");
			info.figure = result.getString("figure");
			info.sex = (result.getString("sex").equals("M")) ? 'M' : 'F';
			info.motto = result.getString("motto");
			
			// Create bot
			SpaceBot bot = new SpaceBot(info);
			bot.X = result.getShort("x");
			bot.Y = result.getShort("y");
			bot.ownerID = result.getShort("ownerid");
			
			// Add to results
			bots.add(bot);
		}
		
		// Close query
		query.close();
		
		return bots;
	}
}
