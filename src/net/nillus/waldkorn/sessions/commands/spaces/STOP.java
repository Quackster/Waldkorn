package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class STOP extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get the key of the status to remove ('stop')
		String status = msg.nextArgument().toLowerCase();
		if(status.equals("carryitem"))
		{
			status = "carryd";
		}
		else if(status.equals("moderator"))
		{
			status = "mod";
		}
		
		// Remove status from room user
		m_session.getSpaceSession().getUser().removeStatus(status);
	}
}
