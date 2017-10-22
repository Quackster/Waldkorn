package net.nillus.waldkorn.items;

import java.sql.ResultSet;

/**
 * Represents a 'template' of a furniture item.
 * 
 * @author Nillus
 */
public class ItemDefinition
{
	/**
	 * The ID of this definition.
	 */
	public int ID;
	/**
	 * The sprite name of this item.
	 */
	public String sprite;
	/**
	 * The color string of this item. (hex colors)
	 */
	public String color;
	/**
	 * The length of this item in tiles.
	 */
	public byte length;
	/**
	 * The width of this item in tiles.
	 */
	public byte width;
	/**
	 * The height the 'top' of this item is located at. Affects the height units and objects are located on when they are 'on top' of this item.
	 */
	public float heightOffset;
	
	/**
	 * The ItemBehaviour object holding the flags for the behaviour of this item.
	 */
	public ItemBehaviour behaviour;
	
	/**
	 * The ingame name of this item.
	 */
	public String name;
	/**
	 * The ingame description of this item.
	 */
	public String description;
	
	public String dataClass;
	
	public static ItemDefinition parse(ResultSet row) throws Exception
	{
		ItemDefinition def = new ItemDefinition();
		def.ID = row.getInt("id");
		def.sprite = row.getString("sprite");
		def.color = row.getString("color");
		def.length = row.getByte("length");
		def.width = row.getByte("width");
		def.heightOffset = row.getFloat("height");
		def.name = row.getString("name");
		def.description = row.getString("description");
		def.dataClass = row.getString("dataclass");
		def.behaviour = ItemBehaviour.parse(row.getString("behaviour"));
		
		return def;
	}
}
