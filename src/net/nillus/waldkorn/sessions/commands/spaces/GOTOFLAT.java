package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceInstance;

public class GOTOFLAT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		int spaceID = Integer.parseInt(msg.nextArgument('/'));
		if(spaceID == m_session.getSpaceSession().authenticatedFlat)
		{
			SpaceInstance instance = m_session.getServer().getSpaceServer().getFlatInstance(spaceID, true);
			if(instance != null)
			{
				m_session.getSpaceSession().enter(instance);
			}
		}
	}
}
