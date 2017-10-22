package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;

public class ADD_FAVORITE_ROOM extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse the ID and get the flat
		int spaceID = Integer.parseInt(msg.nextArgument());
		Space space = m_session.getServer().getSpaceAdmin().getSpaceInfo(spaceID);
		
		// Exists and a user flat?
		if(space != null && space.isUserFlat())
		{
			m_session.getServer().getSpaceAdmin().addToFavoriteFlatListOfUser(m_session.getUserObject().ID, space.ID);
		}
	}
}
