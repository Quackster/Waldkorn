package net.nillus.waldkorn.items;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.storage.Database;

/**
 * An Item is a furniture item that can be either placed in a public space (PassiveObject), in a user flat (ActiveObject) or on the wall of a user flat (WallItem).
 * 
 * @author Nillus
 */
public class Item implements SerializableObject
{
	/**
	 * The ID (number) of this item, unique in the DatabaseEndpoint.
	 */
	public int ID;
	
	/**
	 * The database ID of the User that owns this item.
	 */
	public int ownerID;
	/**
	 * The database ID of the Space this item is in.
	 */
	public int spaceID;
	
	/**
	 * The ItemDefinition object holding information about this item type.
	 */
	public ItemDefinition definition;
	
	/**
	 * The database ID of the other teleporter Item if this Item is a teleporter.
	 */
	public int teleporterID;
	/**
	 * A string holding custom data to the item, such as status etc.
	 */
	public String customData;
	/**
	 * A string holding item data to the Item. ('item' = wallitem) This can be data like text content of a post.it note, but also the display text of a photo.
	 */
	public String itemData;
	
	/**
	 * The X position of this item on the map.
	 */
	public short X;
	/**
	 * The Y position of this item on the map.
	 */
	public short Y;
	/**
	 * The height this item is located at as floating point value.
	 */
	public float Z;
	/**
	 * The rotation of this item.
	 */
	public byte rotation;
	
	/**
	 * The position of this WallItem on the wall of the Space as a String.
	 */
	public String wallPosition;
	
	public static Item parse(ResultSet row, HashMap<Integer, ItemDefinition> definitions) throws Exception
	{
		ItemDefinition definition = definitions.get(row.getInt("definitionid"));
		if(definition == null) return null;
		
		Item item = new Item();
		item.ID = row.getInt("id");
		item.ownerID = row.getInt("ownerid");
		item.definition = definition;
		item.customData = row.getString("customdata");
		item.teleporterID = row.getInt("teleporterid");
		item.itemData = row.getString("itemdata");
		item.spaceID = row.getInt("spaceid");
		item.X = row.getShort("x");
		item.Y = row.getShort("y");
		item.Z = row.getFloat("z");
		item.rotation = row.getByte("rotation");
		item.wallPosition = row.getString("wallposition");
		return item;
	}
	
	public static Item parsePassiveObject(ResultSet row, HashMap<Integer, ItemDefinition> definitions) throws Exception
	{
		ItemDefinition definition = definitions.get(row.getInt("definitionid"));
		if(definition == null) return null;
		
		Item item = new Item();
		item.ID = row.getInt("id");
		item.definition = definition;
		item.itemData = row.getString("object");
		item.customData = row.getString("data");
		item.X = row.getShort("x");
		item.Y = row.getShort("y");
		item.Z = row.getFloat("z");
		item.rotation = row.getByte("rotation");
		return item;
	}
	
	public void update(Database db)
	{
		PreparedStatement query = null;
		try
		{
			query = db.prepareStatement("UPDATE items SET ownerid = ?,spaceid = ?,x = ?,y = ?,z = ?,rotation = ?,customdata = ?,teleporterid = ?,wallposition = ?,itemdata = ? WHERE id = ?;");
			query.setInt(1, this.ownerID);
			query.setInt(2, this.spaceID);
			query.setShort(3, this.X);
			query.setShort(4, this.Y);
			query.setFloat(5, this.Z);
			query.setByte(6, this.rotation);
			if (this.customData == null)
			{
				query.setNull(7, Types.VARCHAR);
			}
			else
			{
				query.setString(7, this.customData);
			}
			query.setInt(8, this.teleporterID);
			if (this.wallPosition == null)
			{
				query.setNull(9, Types.VARCHAR);
			}
			else
			{
				query.setString(9, this.wallPosition);
			}
			if (this.itemData == null)
			{
				query.setNull(10, Types.VARCHAR);
			}
			else
			{
				query.setString(10, this.itemData);
			}
			query.setInt(11, this.ID);
			query.executeUpdate();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			db.releaseStatement(query);
		}
	}
	
	public void delete(Database db)
	{
		try
		{
			db.executeUpdate("DELETE FROM items WHERE id = " + this.ID + ";");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public void serialize(ServerMessage msg)
	{
		if (this.definition.behaviour.onFloor)
		{
			if (this.definition.behaviour.isPassiveObject)
			{
				msg.appendNewArgument(Integer.toString(this.ID));
				msg.appendArgument(this.definition.sprite);
				msg.appendArgument(Integer.toString(this.X));
				msg.appendArgument(Integer.toString(this.Y));
				msg.appendArgument(Integer.toString((int)this.Z));
				msg.appendArgument(Byte.toString(this.rotation));
			}
			else
			{
				msg.appendNewArgument(String.format("%026d", this.ID));
				msg.appendArgument(this.definition.sprite, ',');
				msg.appendArgument(Integer.toString(this.X));
				msg.appendArgument(Integer.toString(this.Y));
				msg.appendArgument(Byte.toString(this.definition.length));
				msg.appendArgument(Byte.toString(this.definition.width));
				msg.appendArgument(Byte.toString(this.rotation));
				msg.appendArgument(Float.toString(this.Z));
				msg.appendArgument(this.definition.color);
				
				msg.appendArgument(this.definition.name, '/');
				msg.appendArgument(this.definition.description, '/');
				
				// Teleporter?
				if (this.teleporterID > 0)
				{
					msg.appendArgument("extr=", '/');
					msg.appendArgument(Integer.toString(this.teleporterID), '/');
				}
				
				// Custom data?
				if (this.customData != null)
				{
					msg.appendArgument(this.definition.dataClass, '/');
					msg.appendArgument(this.customData, '/');
				}
			}
		}
		else if (this.definition.behaviour.onWall)
		{
			/*msg.appendNewArgument(String.format("%022d", this.ID));
			msg.appendArgument(this.definition.sprite, ';');
			msg.appendArgument(null, ';');
			msg.appendArgument(this.wallPosition, ';');
			if (this.customData != null)
			{
				msg.appendPartArgument(this.customData);
			}
			msg.append("\r");
			*/
			
			msg.appendNewArgument("\\" + Integer.toString(this.ID));
			msg.appendArgument(this.definition.sprite, ';');
			msg.appendArgument(null, ';');
			msg.appendArgument(this.wallPosition, ';');
			msg.appendNewArgument(this.customData);
			//# ITEMS 1332621	post.it.vd frontwall -11.0000,-16.8333,11000
		}
	}
	
	public void serialize(ServerMessage msg, int stripSlotID)
	{
		msg.appendNewArgument("SI");
		msg.appendArgument(Integer.toString(this.ID), ';');
		msg.appendArgument(Integer.toString(stripSlotID), ';');
		if (this.definition.behaviour.STUFF)
		{
			msg.appendArgument("S", ';');
		}
		else if (this.definition.behaviour.ITEM)
		{
			msg.appendArgument("I", ';');
		}
		msg.appendArgument(Integer.toString(this.ID), ';');
		msg.appendArgument(this.definition.sprite, ';');
		msg.appendArgument(this.definition.name, ';');
		if (this.definition.behaviour.STUFF)
		{
			msg.appendArgument(this.customData, ';');
			msg.appendArgument(Integer.toString(this.definition.length), ';');
			msg.appendArgument(Integer.toString(this.definition.width), ';');
			msg.appendArgument(this.definition.color, ';');
		}
		else if (this.definition.behaviour.ITEM)
		{
			msg.appendArgument(this.customData, ';');
			msg.appendArgument(this.definition.name, ';');
		}
		msg.append("/");
	}
}
