package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class Move extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Gather destination tile coordinates
		short tileX = Short.parseShort(msg.nextArgument());
		short tileY = Short.parseShort(msg.nextArgument());
		
		// Get SpaceUser
		SpaceUser usr = m_session.getSpaceSession().getUser();
		
		// Can move?
		if (usr != null && !usr.moveLock)
		{
			// Not trying to restart the route? (as it's pointless...)
			if (tileX != usr.goalX || tileY != usr.goalY)
			{
				// Not clicking own tile?
				if (tileX != usr.X || tileY != usr.Y)
				{
					// Request movement 'trip' in space instance
					// Pathfinder will be invoked in the current thread (so, m_thread in TcpConnection)
					m_session.getSpaceSession().getSpace().getInteractor().startUserMovement(usr, tileX, tileY, false);
				}
			}
		}
	}
}