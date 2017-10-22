package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class GETSTRIP extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		String mode = msg.getBody();
		m_session.getSpaceSession().getInventory().sendStrip(mode);
	}
}
