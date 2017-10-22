package net.nillus.waldkorn.sessions.commands.master;

import java.util.Vector;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;

public class SEARCHFLAT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Filter additional wildcards from search criteria
		String criteria = msg.nextArgument('/').replace("%", "");
		
		// Perform the flat search
		Vector<Space> flats = m_session.getServer().getSpaceAdmin().searchFlats(criteria);
		
		// Serialize the search result
		if (flats.size() == 0)
		{
			m_response.set("NOFLATS");
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
