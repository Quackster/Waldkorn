package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class PLACESTUFFFROMSTRIP extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get item from inventory
		int itemID = Integer.parseInt(msg.nextArgument());
		short tileX = Short.parseShort(msg.nextArgument());
		short tileY = Short.parseShort(msg.nextArgument());
		byte rotation = Byte.parseByte(msg.nextArgument());
		Item item = m_session.getSpaceSession().getInventory().getItem(itemID);
		
		// Valid item?
		if (item == null || !item.definition.behaviour.onFloor)
		{
			return;
		}
		
		// Trying to place down without flat controller?
		if (!m_session.getSpaceSession().getUser().isFlatController)
		{
			return;
		}
		
		// Place the object in the flat and remove it from the inventory when OK
		if (m_session.getSpaceSession().getSpace().getInteractor().moveActiveObject(itemID, item, tileX, tileY, (byte)0))
		{
			m_session.getSpaceSession().getInventory().removeItem(itemID);
		}
	}
}
