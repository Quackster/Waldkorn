package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.items.ItemTrader;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class TRADE_ADDITEM extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Trading?
		ItemTrader me = m_session.getSpaceSession().getTrader();
		if (me.busy())
		{
			// Get item to offer
			int itemID = Integer.parseInt(msg.nextArgument((char)9));
			Item item = m_session.getSpaceSession().getInventory().getItem(itemID);
			
			// Item in inventory?
			if (item != null)
			{
					// Add item to this User's offer
					me.offerItem(item);
					
					// Refresh clients
					me.refresh();
					me.getPartner().refresh();
			}
		}
	}
}
