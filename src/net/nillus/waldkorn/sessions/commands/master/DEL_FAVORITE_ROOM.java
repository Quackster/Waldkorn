package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class DEL_FAVORITE_ROOM extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse the ID
		int spaceID = Integer.parseInt(msg.nextArgument());
		
		// Issue removal
		m_session.getServer().getSpaceAdmin().removeFromFavoriteFlatListOfUser(m_session.getUserObject().ID, spaceID);
	}
}
