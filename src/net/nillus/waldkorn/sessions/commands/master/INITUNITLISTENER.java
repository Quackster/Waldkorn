package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceInstance;

public class INITUNITLISTENER extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		m_response.set("ALLUNITS");
		
		for (SpaceInstance unit : m_session.getServer().getSpaceServer().getUnitInstances())
		{
			m_response.appendObject(unit.getInfo());
		}
		
		sendResponse();
	}
}
