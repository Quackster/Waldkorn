package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGER_ASSIGNPERSMSG extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get new message
		String newMessage = msg.getBody();
		
		// Update user object
		m_session.getUserObject().messengerMotto = newMessage;
		m_session.getUserObject().updateInfo(m_session.getServer().getDatabase());
	}
}
