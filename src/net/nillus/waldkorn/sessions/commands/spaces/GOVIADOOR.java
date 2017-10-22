package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceInstance;

public class GOVIADOOR extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse data
		int spaceID = Integer.parseInt(msg.nextArgument('/'));
		int itemID = Integer.parseInt(msg.nextArgument('/'));
		
		// Get instance of Space
		SpaceInstance instance = m_session.getServer().getSpaceServer().getFlatInstance(spaceID, true);
		if (instance != null)
		{
			m_session.getSpaceSession().authenticatedTeleporter = itemID;
			m_session.getSpaceSession().enter(instance);
		}
	}
}
