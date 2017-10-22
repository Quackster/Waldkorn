package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class ASSIGNRIGHTS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get this user
		SpaceUser usr = m_session.getSpaceSession().getUser();
		
		// In order to assign rights, you require flat owner
		if(usr.isFlatAdmin)
		{
			// Get target user
			SpaceUser usr2 = m_session.getSpaceSession().getSpace().getUserByName(msg.nextArgument());
			
			// Verify that target user exists
			if(usr2 != null)
			{
				// Try to add
				if(m_session.getSpaceSession().getSpace().addFlatController(usr2.getUserObject().ID))
				{
					// Mark his SpaceUser instance as flat controller and resend user status
					usr2.isFlatController = true;
					usr2.refreshFlatPrivileges();
				}
			}
		}
	}
}
