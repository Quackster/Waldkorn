package net.nillus.waldkorn.spaces;

import java.sql.ResultSet;
import java.util.Vector;

import net.nillus.waldkorn.items.Item;

public class SpaceModel
{
	/**
	 * The type string of this model, eg, 'model_a', 'pool_a', 'kitchen'.
	 */
	public String type;
	
	/**
	 * The X position of the door in this model on the map.
	 */
	public short doorX;
	/**
	 * The Y position of the door in this model on the map.
	 */
	public short doorY;
	/**
	 * The Z (height) position new users get spawned at when they enter a space with this model.
	 */
	public float doorZ;
	/**
	 * The default heightmap string of this model's map, each 'new line' is delimited by a whitespace.
	 */
	public String defaultHeightmap;
	/**
	 * True if this model has a swimming pool, so pool clothes of users have to be visible to clients.
	 */
	public boolean hasSwimmingPool;
	
	private Vector<Item> m_passiveObjects;
	
	public void setPassiveObjects(Vector<Item> objs)
	{
		m_passiveObjects = objs;
	}
	
	public Vector<Item> getPassiveObjects()
	{
		return m_passiveObjects;
	}
	
	public static SpaceModel parse(ResultSet row) throws Exception
	{
		SpaceModel model = new SpaceModel();
		model.type = row.getString("model");
		model.doorX = row.getShort("doorx");
		model.doorY = row.getShort("doory");
		model.doorZ = row.getFloat("doorz");
		model.defaultHeightmap = row.getString("heightmap");
		model.hasSwimmingPool = row.getBoolean("haspool");
		return model;
	}
}
