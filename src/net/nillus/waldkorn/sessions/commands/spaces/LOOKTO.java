package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class LOOKTO extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Gather coordinates of tile to make avatar 'look to'
		short tileX = Short.parseShort(msg.nextArgument());
		short tileY = Short.parseShort(msg.nextArgument());
		
		// Get user
		SpaceUser usr = m_session.getSpaceSession().getUser();
		
		// Can not rotate while moving
		if(usr.goalX == -1)
		{
			// Cannot click self
			if(!(usr.X == tileX && usr.Y == tileY))
			{
				usr.lookTo(tileX, tileY);
			}
		}
	}
}
