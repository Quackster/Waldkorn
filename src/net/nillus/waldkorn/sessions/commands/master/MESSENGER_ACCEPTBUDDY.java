package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGER_ACCEPTBUDDY extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get the name of the user to accept
		String name = msg.nextArgument();
		
		// Requester exists?
		m_session.getMasterSession().getMessenger().acceptBuddyRequest(name);
	}
}
