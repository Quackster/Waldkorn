package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGER_DECLINEBUDDY extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get the name of the user to decline
		String name = msg.nextArgument();
	
		// Delete the buddy request
		m_session.getMasterSession().getMessenger().declineBuddyRequest(name);
	}
}
