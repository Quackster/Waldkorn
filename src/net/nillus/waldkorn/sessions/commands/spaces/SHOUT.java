package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;
import net.nillus.waldkorn.util.ChatCommandParser;
import net.nillus.waldkorn.util.SecurityUtil;

public class SHOUT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get text message
		String text = SecurityUtil.filterInput(msg.getBody());
		if (text.trim().length() > 0)
		{
			// Check chat commands
			if (!ChatCommandParser.parseCommand(m_session.getSpaceSession(), text))
			{
				// Broadcast shout and animate this user
				SpaceUser usr = m_session.getSpaceSession().getUser();
				m_session.getSpaceSession().getSpace().shout(usr, text);
				
				// Waving!
				if(text.startsWith("o/"))
				{
					usr.wave();
				}
			}
		}
	}
}
