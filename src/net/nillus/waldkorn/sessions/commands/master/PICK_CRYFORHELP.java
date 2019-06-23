package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.Waldkorn;
import net.nillus.waldkorn.moderation.ModerationCenter;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class PICK_CRYFORHELP extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse call ID from chatlog URL
		int callID = ModerationCenter.parseCallID(msg.getBody());
		
		// Valid chatlog URL?
		if(callID != -1)
		{
			// Not picked up already?
			if(!Waldkorn.getServer().getModerationCenter().pickCallForHelp(callID, m_session.getUserObject().name))
			{
				m_session.systemMsg("This call has already been picked up!");
			}
		}
	}
}
