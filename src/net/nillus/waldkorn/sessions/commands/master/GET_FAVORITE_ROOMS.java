package net.nillus.waldkorn.sessions.commands.master;

import java.util.Vector;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;

public class GET_FAVORITE_ROOMS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get the favorite flat list
		Vector<Space> flats = m_session.getServer().getSpaceAdmin().getFavoriteFlatListForUser(m_session.getUserObject().ID);
		
		// Build response
		m_response.set("FAVORITE_FLAT_RESULTS");
		for (Space flat : flats)
		{
			m_response.appendObject(flat);
		}
		
		// Send response
		sendResponse();
	}
}
