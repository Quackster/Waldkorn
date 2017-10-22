package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class KILLUSER extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get this user
		SpaceUser usr = m_session.getSpaceSession().getUser();
		
		// In order to kick users, you require flat controller
		if (usr.isFlatController)
		{
			// Get target user
			SpaceUser usr2 = m_session.getSpaceSession().getSpace().getUserByName(msg.nextArgument());
			
			// Verify that target user exists
			if (usr2 != null)
			{
				// If it's a flat owner, then you need atleast the same user role!
				if (!usr2.isFlatAdmin || usr.getUserObject().role >= usr2.getUserObject().role)
				{
					usr2.getSession().requestRemoval("kicked by " + m_session.getUserObject().name);
				}
			}
		}
	}
}
