package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MOVESTUFF extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Is this user a flat controller?
		if (m_session.getSpaceSession().getUser().isFlatController)
		{
			// Parse the data
			int itemID = Integer.parseInt(msg.nextArgument());
			short newX = Short.parseShort(msg.nextArgument());
			short newY = Short.parseShort(msg.nextArgument());
			byte newRotation = (msg.remainingBodyLength() > 0) ? Byte.parseByte(msg.nextArgument()) : 0;
			
			// Attempt move
			if(!m_session.getSpaceSession().getSpace().getInteractor().moveActiveObject(itemID, null, newX, newY, newRotation))
			{
				// Moving failed? Well atleast refresh for client then
				Item obj = m_session.getSpaceSession().getSpace().getInteractor().getActiveObject(itemID);
				if(obj != null)
				{
					m_response.set("ACTIVEOBJECT_UPDATE");
					m_response.appendObject(obj);
					sendResponse();
				}
			}
		}
	}
}
