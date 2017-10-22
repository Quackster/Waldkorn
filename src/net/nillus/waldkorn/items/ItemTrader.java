package net.nillus.waldkorn.items;

import java.util.Vector;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.spaces.SpaceSession;

/**
 * Provides 'trading Items' with other Users.\r Operates together with ItemInventory.
 * 
 * @author Nillus
 */
public class ItemTrader implements SerializableObject
{
	private SpaceSession m_session;
	private SpaceSession m_partner;
	
	private Vector<Item> m_offer;
	private boolean m_accept;
	
	public ItemTrader(SpaceSession session)
	{
		m_session = session;
		m_offer = new Vector<Item>();
	}
	
	public void open(SpaceSession partner)
	{
		m_partner = partner;
	}
	
	public void close()
	{
		// Clear variables
		m_accept = false;
		synchronized (m_offer)
		{
			m_offer.clear();
		}
		m_partner = null;
		
		// Close window for client
		m_session.send(new ServerMessage("TRADE_CLOSE"));
		
		// Remove 'trading' status in space
		m_session.getUser().removeStatus("trd");
	}
	
	public void abort()
	{
		if(this.busy())
		{
			// Close trade handlers for npch clients
			m_partner.getTrader().close();
			this.close();
		}
	}
	
	public void accept()
	{
		m_accept = true;
	}
	
	public void unaccept()
	{
		m_accept = false;
	}
	
	public void offerItem(Item item)
	{
		m_accept = false;
		synchronized (m_offer)
		{
			m_offer.add(item);
		}
	}
	
	public void castOfferAway()
	{
		// Actually trading?
		if(!busy())
		{
			return;
		}
		
		synchronized (m_offer)
		{
			for (Item item : m_offer)
			{
				// Remove item from 'my' inventory
				m_session.getInventory().removeItem(item.ID);
				
				// Add item to partner's inventory
				item.ownerID = m_partner.getUserObject().ID;
				m_partner.getInventory().addItem(item);
				
				// Update Item
				item.update(m_session.getServer().getDatabase());
			}
		}
	}
	
	public void refresh()
	{
		if (m_partner != null)
		{
			ServerMessage msg = new ServerMessage("TRADE_ITEMS");
			msg.appendObject(this);
			msg.appendObject(m_partner.getTrader());
			
			this.getSession().send(msg);
		}
	}
	
	public boolean accepting()
	{
		return m_accept;
	}
	
	public ItemTrader getPartner()
	{
		return m_partner.getTrader();
	}
	
	/**
	 * True if this ItemTrader is currently busy, False otherwise.
	 */
	public boolean busy()
	{
		return (m_partner != null);
	}
	
	/**
	 * Returns the Vector collection holding the Item objects that this User currently offers in trade.
	 */
	public Vector<Item> getOffer()
	{
		return m_offer;
	}
	
	/**
	 * Returns the SpaceSession linked to this ItemTrader.
	 */
	public SpaceSession getSession()
	{
		return m_session;
	}
	
	@Override
	public void serialize(ServerMessage msg)
	{
		// 'User' and 'accept state'
		msg.appendNewArgument(m_session.getUserObject().name);
		msg.appendTabArgument(Boolean.toString(m_accept));
		
		// Items offered by this User
		int index = 0;
		synchronized (m_offer)
		{
			for (Item item : m_offer)
			{
				item.serialize(msg, index++);
			}
		}
		
		// End of this 'box'
		msg.appendNewArgument(Integer.toString(m_offer.size()));
	}
}
