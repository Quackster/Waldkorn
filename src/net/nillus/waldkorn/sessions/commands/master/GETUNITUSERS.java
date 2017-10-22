package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceInstance;
import net.nillus.waldkorn.spaces.SpaceUser;

public class GETUNITUSERS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get space instance
		String unitName = msg.nextArgument('/');
		SpaceInstance instance = m_session.getServer().getSpaceServer().getUnitInstance(unitName);
		
		m_response.set("UNITMEMBERS");
		if (instance != null)
		{
			for (SpaceUser usr : instance.getUsers())
			{
				m_response.appendNewArgument(usr.getUserObject().name);
			}
		}
		sendResponse();
	}
}
