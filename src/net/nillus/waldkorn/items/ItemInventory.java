package net.nillus.waldkorn.items;

import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.Map.Entry;

import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.spaces.SpaceSession;

/**
 * ItemStripHandler holds the Items of a User while in a SpaceInstance.
 * 
 * @author Nillus
 */
public class ItemInventory
{
	private static final int ITEMSTRIP_ITEMS_ON_PAGE = 9;
	
	// session
	private SpaceSession m_session;
	
	// Item strip
	private LinkedHashMap<Integer, Item> m_items;
	
	private byte m_stripPage;
	private boolean m_stripItemsLoaded;
	
	public ItemInventory(SpaceSession session)
	{
		m_session = session;
		m_items = new LinkedHashMap<Integer, Item>();
	}
	
	public void clear()
	{
		m_items.clear();
		
		m_stripPage = 0;
		m_stripItemsLoaded = false;
	}
	
	public void loadStripItems()
	{
		if (!m_stripItemsLoaded)
		{
			Vector<Item> items = m_session.getMasterSession().getServer().getItemAdmin().getUserItemInventory(m_session.getUserObject().ID);
			for(Item item : items)
			{
				m_items.put(item.ID, item);
			}
			m_stripItemsLoaded = true;
		}
	}
	
	public void addItem(Item item)
	{
		m_items.put(item.ID, item);
	}
	
	public void removeItem(int itemID)
	{
		m_items.remove(itemID);
	}
	
	public boolean containsItem(int itemID)
	{
		return m_items.containsKey(itemID);
	}
	
	public Item getItem(int itemID)
	{
		return m_items.get(itemID);
	}
	
	public Item getItemWithSprite(String sprite)
	{
		for(Item item : m_items.values())
		{
			if(item.definition.sprite.equals(sprite))
			{
				return item;
			}
		}
		
		return null;
	}
	
	public void resetStripPage()
	{
		m_stripPage = 0;
	}
	
	public void sendStrip(String mode)
	{
		if (mode.equals("new"))
		{
			m_stripPage = 0;
		}
		else if (mode.equals("next"))
		{
			m_stripPage++;
		}
		else if (mode.equals("last"))
		{
			m_stripPage = (byte)((m_items.size() - 1) / ITEMSTRIP_ITEMS_ON_PAGE);
		}
		
		ServerMessage msg = new ServerMessage("STRIPINFO ");
		int start = 0;
		int end = m_items.size();
		
		// Actually items in the hand?
		if (end > 0)
		{
			// Determine range of items to show on this 'page'
			start = m_stripPage * ITEMSTRIP_ITEMS_ON_PAGE;
			if (end > (start + ITEMSTRIP_ITEMS_ON_PAGE))
			{
				end = start + ITEMSTRIP_ITEMS_ON_PAGE;
			}
			while (start >= end)
			{
				m_stripPage--;
				start = m_stripPage * ITEMSTRIP_ITEMS_ON_PAGE;
				if (end > (start + ITEMSTRIP_ITEMS_ON_PAGE))
				{
					end = start + ITEMSTRIP_ITEMS_ON_PAGE;
				}
			}
			
			// Serialize items within that range
			/*
			for(int index = start; index < end; index++)
			{
				m_itemsForIteration.get(index).serialize(msg);
			}*/
			
			int index = 0;
			for(Entry<Integer, Item> pair : m_items.entrySet())
			{
				if(index >= start && index < end)
				{
					pair.getValue().serialize(msg, index);
				}
				index++;
			}
		}
		
		// Total amount of items
		msg.appendNewArgument(Integer.toString(m_items.size()));
		
		// Send to session
		m_session.send(msg);
	}
	
	public int count()
	{
		return m_items.size();
	}
	
	public SpaceSession getSession()
	{
		return m_session;
	}
}
