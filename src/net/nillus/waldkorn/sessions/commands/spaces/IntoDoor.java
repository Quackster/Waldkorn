package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class IntoDoor extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get teleporter
		int itemID = Integer.parseInt(msg.getBody());
		Item obj = m_session.getSpaceSession().getSpace().getInteractor().getActiveObject(itemID);
		
		// Is this a teleporter?
		if (obj != null && obj.definition.behaviour.isTeleporter)
		{
			// Get user by client ID
			SpaceUser usr = m_session.getSpaceSession().getUser();
			if (usr != null)
			{
				// Can enter teleporter from this position?
				if (((obj.rotation == 0 || obj.rotation == 2) && ((usr.X == obj.X + 1) && (usr.Y == obj.Y))) || (obj.rotation == 4 && ((usr.X == obj.X) && (usr.Y == obj.Y + 1))))
				{
					m_session.getSpaceSession().getSpace().getInteractor().startUserMovement(usr, obj.X, obj.Y, true);
				}
			}
		}
	}
}
