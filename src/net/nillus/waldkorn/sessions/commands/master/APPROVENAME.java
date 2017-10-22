package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class APPROVENAME extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get name from message
		String name = msg.nextArgument();
		
		// Set response msg type depending on result of the check
		if (m_session.getServer().getUserRegister().approveName(name) == true)
		{
			m_response.set("NAME_APPROVED");
		}
		else
		{
			m_response.set("NAME_UNACCEPTABLE");
		}
		
		// Send the response
		sendResponse();
	}
}
