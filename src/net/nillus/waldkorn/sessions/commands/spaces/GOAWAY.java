package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class GOAWAY extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Should move to door and then leave, but oh well
		m_session.getSpaceSession().exit(null);
	}
}
