package net.nillus.waldkorn.spaces.sql;

import com.blunk.storage.sql.SQLDataQuery;
import net.nillus.waldkorn.Waldkorn;
import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.spaces.SpaceModelObjectsLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class SQLSpaceModelObjectsLoader extends SpaceModelObjectsLoader implements SQLDataQuery
{
	@Override
	public void execute(Connection conn) throws SQLException
	{
		
	}
	
	@Override
	public Vector<?> query(Connection conn) throws SQLException
	{
		PreparedStatement query = null;
		if (super.modelType != null)
		{
			query = conn.prepareStatement("SELECT * FROM spaces_models_objects WHERE model = ?;");
			query.setString(1, super.modelType);
		}
		// Execute query
		ResultSet result = query.executeQuery();
		
		// Fetch results
		Vector<Item> objs = new Vector<Item>();
		while (result.next())
		{
			Item obj = new Item();
			if (SQLSpaceModelObjectsLoader.parseObjectFromResultSet(obj, result))
			{
				objs.add(obj);
			}
		}
		
		query.close();
		
		// Return bucket
		return objs;
	}
	
	public static boolean parseObjectFromResultSet(Item obj, ResultSet result)
	{
		try {
			obj.definition = Waldkorn.getServer().getItemAdmin().getDefinitions().get(result.getInt("definitionid"));

			obj.ID = result.getInt("id");
			obj.X = result.getShort("x");
			obj.Y = result.getShort("y");
			obj.Z = result.getFloat("z");
			obj.rotation = result.getByte("rotation");
			obj.itemData = result.getString("object");
			obj.customData = result.getString("data");

			// Parsed OK!
			return true;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
}
