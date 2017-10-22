package net.nillus.waldkorn.sessions.commands.master;

import java.util.Vector;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;


public class SEARCHFLATFORUSER extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		Vector<Space> flats = m_session.getServer().getSpaceAdmin().searchFlatsForUser(m_session.getUserObject().name);
		if (flats.size() == 0)
		{
			m_response.set("NOFLATSFORUSER");
		}
		else
		{
			m_response.set("FLAT_RESULTS");
			for (Space flat : flats)
			{
				m_response.appendObject(flat);
			}
		}
		
		sendResponse();
	}
}
