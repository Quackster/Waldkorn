package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGER_SENDUPDATE extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Update last activity to NOW
		m_session.getUserObject().updateInfo(m_session.getServer().getDatabase());
		
		// Re-send buddy list
		m_session.getMasterSession().getMessenger().sendBuddyList();
	}
}
