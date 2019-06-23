package com.suelake.habbo.spaces.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.blunk.Log;
import com.blunk.storage.DatabaseException;
import com.blunk.storage.sql.SQLDataQuery;
import com.suelake.habbo.spaces.SpaceModel;
import com.suelake.habbo.spaces.SpaceModelLoader;

public class SQLSpaceModelLoader extends SpaceModelLoader implements SQLDataQuery
{
	@Override
	public void execute(Connection conn) throws DatabaseException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Vector<SpaceModel> query(Connection conn) throws SQLException
	{
		// Execute the query
		ResultSet result = conn.createStatement().executeQuery("SELECT * FROM spaces_models;");
		
		// Parse the results
		Vector<SpaceModel> models = new Vector<SpaceModel>();
		while(result.next())
		{
			SpaceModel model = SQLSpaceModelLoader.parse(result);
			if(model != null)
			{
				models.add(model);
			}
		}
		
		result.close();
		
		return models;
	}
	
	public static final SpaceModel parse(ResultSet result)
	{
		SpaceModel model = new SpaceModel();
		try
		{
			model.type = result.getString("model");
			model.doorX = result.getShort("doorx");
			model.doorY = result.getShort("doory");
			model.doorZ = result.getFloat("doorz");
			model.defaultHeightmap = result.getString("heightmap");
			model.hasSwimmingPool = result.getBoolean("haspool");
			
			return model;
		}
		catch (Exception ex)
		{
			Log.error("Could not fully parse net.scriptomatic.habbo.spaces.SpaceModel from given java.sql.ResultSet, probably fields missing or bad data!", ex);
		}
		
		return null;
	}
}
