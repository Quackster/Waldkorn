package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class REMOVESTUFF extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Can only do this with flat controller
		if(!m_session.getSpaceSession().getUser().isFlatController)
		{
			return;
		}
		
		// Get the item to delete permanently
		int itemID = Integer.parseInt(msg.nextArgument('/'));
		Item obj = m_session.getSpaceSession().getSpace().getInteractor().pickupActiveObject(itemID);
		
		// Picked up? Then delete...
		if(obj != null)
		{
			obj.delete(m_session.getServer().getDatabase());
		}
	}
}
