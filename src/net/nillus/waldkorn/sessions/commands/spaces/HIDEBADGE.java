package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.Waldkorn;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;
import net.nillus.waldkorn.spaces.SpaceUser;

public class HIDEBADGE extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		SpaceUser usr = m_session.getSpaceSession().getUser();
		usr.removeStatus("mod");
	}
}
