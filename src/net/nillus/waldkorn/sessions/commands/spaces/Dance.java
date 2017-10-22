package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class Dance extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get user
		SpaceUser usr = m_session.getSpaceSession().getUser();
		
		// Can dance in current context?
		if(!usr.hasStatus("sit") && !usr.hasStatus("lay"))
		{
			// Handle statuses
			usr.removeStatus("carryd");
			usr.removeStatus("drink");
			usr.removeStatus("wave");
			usr.addStatus("dance", null, 0, null, 0, 0);
		}
	}
}
