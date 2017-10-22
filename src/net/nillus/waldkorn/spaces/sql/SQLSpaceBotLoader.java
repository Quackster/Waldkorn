package net.nillus.waldkorn.spaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import net.nillus.waldkorn.spaces.Npc;
import net.nillus.waldkorn.users.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.storage.Database;

import com.blunk.storage.sql.SQLDataQuery;

public class SQLNpcLoader extends NpcLoader implements SQLDataQuery
{
	@Override
	public void execute(Connection conn) throws SQLException
	{
		
	}
	
	@Override
	public Vector<?> query(Connection conn) throws SQLException
	{
		// Create query
		PreparedStatement query = conn.prepareStatement("SELECT * FROM spaces_npcs WHERE spaceid = ?;");
		query.setInt(1, super.spaceID);
		
		// Execute query
		ResultSet result = query.executeQuery();
		Vector<Npc> npcs = new Vector<Npc>(0);
		
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
			
			// Create npc
			Npc npc = new Npc(info);
			npc.X = result.getShort("x");
			npc.Y = result.getShort("y");
			npc.ownerID = result.getShort("ownerid");
			
			// Add to results
			npcs.add(npc);
		}
		
		// Close query
		query.close();
		
		return npcs;
	}
}
