package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;
import net.nillus.waldkorn.util.SecurityUtil;

public class WHISPER extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get receiver & text message
		String receiver = msg.nextArgument();
		String text = SecurityUtil.filterInput(msg.getRemainingBody());
		
		// Get receiver user
		SpaceUser usr2 = m_session.getSpaceSession().getSpace().getUserByName(receiver);
		if (usr2 != null)
		{
			// Prepare whisper message
			ServerMessage whisper = new ServerMessage("WHISPER");
			whisper.appendNewArgument(m_session.getUserObject().name);
			whisper.appendArgument(text);
			
			// Send to this user and receiver
			m_session.getSpaceSession().send(whisper);
			usr2.getSession().send(whisper);
		}
	}
}
