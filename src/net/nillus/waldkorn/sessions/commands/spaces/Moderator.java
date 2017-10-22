package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class Moderator extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		String badge = m_session.getUserObject().badge;
		if(badge != null)
		{
			m_session.getSpaceSession().getUser().addStatus("mod", badge, 0, null, 0, 0);
		}
	}
}
