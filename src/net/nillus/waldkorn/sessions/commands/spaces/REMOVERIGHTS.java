package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class REMOVERIGHTS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get this user
		SpaceUser usr = m_session.getSpaceSession().getUser();
		
		// In order to remove rights, you require flat owner
		if(usr.isFlatAdmin)
		{
			// Get target user
			SpaceUser usr2 = m_session.getSpaceSession().getSpace().getUserByName(msg.nextArgument());
			
			// Verify that target user exists
			if(usr != null)
			{
				// Target is not owner? (Cannot remove owner rights)
				if(!usr2.isFlatAdmin)
				{
					// Attempt to remove
					if(m_session.getSpaceSession().getSpace().removeFlatController(usr2.getUserObject().ID))
					{
						// Mark his SpaceUser instance as not-flat controller and resend user status
						usr2.isFlatController = false;
						usr2.refreshFlatPrivileges();
						usr2.getSession().send(new ServerMessage("YOUARENOTCONTROLLER"));
					}
				}
			}
		}
	}
}
