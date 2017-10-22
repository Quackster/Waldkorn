package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGER_MARKREAD extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		int messageID = Integer.parseInt(msg.nextArgument());
		m_session.getMasterSession().getMessenger().markMessageAsRead(messageID);
	}
}
