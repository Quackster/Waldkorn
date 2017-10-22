package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGER_REMOVEBUDDY extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get the buddy to remove
		String name = msg.nextArgument();
		
		// Process the removal
		m_session.getMasterSession().getMessenger().requestBuddyRemoval(name);
	}
}
