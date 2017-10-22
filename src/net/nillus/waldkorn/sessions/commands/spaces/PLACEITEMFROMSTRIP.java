package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class PLACEITEMFROMSTRIP extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get item from inventory
		int itemID = Integer.parseInt(msg.nextArgument());
		String position = msg.nextArgument('/');
		Item item = m_session.getSpaceSession().getInventory().getItem(itemID);
		
		// Valid item?
		if (item == null || !item.definition.behaviour.onWall || item.definition.behaviour.isPostIt)
		{
			return;
		}
		
		// Trying to place down without flat controller?
		if (!m_session.getSpaceSession().getUser().isFlatController)
		{
			return;
		}
		
		// Attempt to place item
		if (m_session.getSpaceSession().getSpace().getInteractor().placeWallItem(item, position))
		{
			m_session.getSpaceSession().getInventory().removeItem(itemID);
		}
	}
}
