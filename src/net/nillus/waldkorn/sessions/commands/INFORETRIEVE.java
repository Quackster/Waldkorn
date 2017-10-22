package net.nillus.waldkorn.sessions.commands;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class INFORETRIEVE extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		m_response.set("USEROBJECT");
		m_response.appendObject(m_session.getMasterSession().getUserObject());
		sendResponse();
	}
}
