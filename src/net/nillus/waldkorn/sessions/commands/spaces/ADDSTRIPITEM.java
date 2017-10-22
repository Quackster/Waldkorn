package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class ADDSTRIPITEM extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Is this user a flat owner?
		if(m_session.getSpaceSession().getUser().isFlatAdmin)
		{
			// Parse data
			String event = msg.nextArgument();
			String type = msg.nextArgument();
			int itemID = Integer.parseInt(msg.nextArgument());
			
			// Attempt to pickup
			Item item = null;
			if(type.equals("stuff"))
			{
				item  = m_session.getSpaceSession().getSpace().getInteractor().pickupActiveObject(itemID);
			}
			else if(type.equals("item"))
			{
				item = m_session.getSpaceSession().getSpace().getInteractor().pickupWallItem(itemID);
			}
			
			// Pickup OK?
			if(item != null)
			{
				// Item is this user's inventory now!
				item.ownerID = m_session.getUserObject().ID;
				m_session.getSpaceSession().getInventory().addItem(item);
				m_session.getSpaceSession().getInventory().sendStrip("last");
				
				// Update item in DatabaseEndpoint
				item.update(m_session.getServer().getDatabase());
			}
		}
	}
}
