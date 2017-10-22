package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class GETADFORME extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		m_response.set("ADVERTISEMENT");
		m_response.appendArgument("0");
		
		sendResponse();
	}
}
