package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGER_REQUESTBUDDY extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get name of the user to request
		String name = msg.nextArgument((char)13);
		
		// Doo eeet...
		m_session.getMasterSession().getMessenger().createBuddyRequest(name);
	}
}
