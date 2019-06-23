package net.nillus.waldkorn.items;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Vector;

import net.nillus.waldkorn.MasterServer;
import net.nillus.waldkorn.ServerComponent;

/**
 * ItemAdministration is a manager for Items.
 * 
 * @author Nillus
 */
public class ItemAdministration extends ServerComponent
{
	private HashMap<Integer, ItemDefinition> m_definitions;
	
	public ItemAdministration(MasterServer server)
	{
		super(server);
		m_definitions = new HashMap<Integer, ItemDefinition>();
	}
	
	public ItemDefinition getDefinition(int definitionID)
	{
		return m_definitions.get(definitionID);
	}
	
	public int loadItemDefinitions()
	{
		// Clear current values
		m_definitions.clear();
		
		// Parse new values from database
		ResultSet result = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT * FROM items_definitions ORDER BY id ASC;");
			while (result.next())
			{
				ItemDefinition def = ItemDefinition.parse(result);
				m_definitions.put(def.ID, def);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		
		// Log how many loaded
		m_server.getLogger().info("ItemAdministration", "loaded " + m_definitions.size() + " item definitions");
		return m_definitions.size();
	}
	
	private final static String[] PRESENTBOX_SPRITES = { "present_gen1", "present_gen2", "present_gen3", "present_gen4", "present_gen5", "present_gen6" };
	
	public ItemDefinition getPresentBoxDefinition()
	{
		String sprite = PRESENTBOX_SPRITES[getServer().getRandom().nextInt(PRESENTBOX_SPRITES.length)];
		
		for (ItemDefinition def : m_definitions.values())
		{
			if (def.sprite.equals(sprite))
			{
				return def;
			}
		}
		
		return null;
	}
	
	public Item getItem(int itemID)
	{
		ResultSet result = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT * FROM items WHERE id = " + itemID + ";");
			if(result.next())
			{
				return Item.parse(result, m_definitions);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		return null;
	}
	
	public Vector<Item> getUserItemInventory(int userID)
	{
		ResultSet result = null;
		Vector<Item> items = new Vector<Item>();
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT * FROM items WHERE ownerid = " + userID + " AND spaceid = 0;");
			while (result.next())
			{
				Item item = Item.parse(result, m_definitions);
				if(item != null)
				{
					items.add(item);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		return items;
	}
	
	public Vector<Item> getSpaceItems(int spaceID)
	{
		ResultSet result = null;
		Vector<Item> items = new Vector<Item>();
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT * FROM items WHERE spaceid = " + spaceID + ";");
			while (result.next())
			{
				Item item = Item.parse(result, m_definitions);
				if(item != null)
				{
					items.add(item);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		
		return items;
	}
	
	public boolean storeItem(Item item)
	{
		PreparedStatement query = null;
		try
		{
			// Insert the item into the database
			query = m_server.getDatabase().prepareStatement("INSERT INTO items(definitionid,ownerid,customdata) VALUES (?,?,?);");
			query.setInt(1, item.definition.ID);
			query.setInt(2, item.ownerID);
			if(item.customData == null)
			{
				query.setNull(3, Types.VARCHAR);
			}
			else
			{
				query.setString(3, item.customData);
			}
			query.executeUpdate();
			
			// Fetch the generated keys
			ResultSet keys = query.getGeneratedKeys();
			if(keys.next())
			{
				item.ID = keys.getInt(1);
				return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(query);
		}
		
		// Insertion failed!
		return false;
	}

	public HashMap<Integer, ItemDefinition> getDefinitions()
	{
		return m_definitions;
	}
}
