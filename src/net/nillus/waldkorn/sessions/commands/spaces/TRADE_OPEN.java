package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class TRADE_OPEN extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{		
		// Get name of the 'target user'
		String name = msg.nextArgument((char)9);
		
		// Get self and target user
		SpaceUser usr = m_session.getSpaceSession().getUser();
		SpaceUser usr2 = m_session.getSpaceSession().getSpace().getUserByName(name);
		
		// Both users exist?
		if(usr != null && usr2 != null)
		{
			// Not trading yet?
			if(!m_session.getSpaceSession().getTrader().busy())
			{
				// Partner not trading yet?
				if(!usr2.getSession().getTrader().busy())
				{
					// Add trading statuses
					usr.addStatus("trd", null, 0, null, 0, 0);
					usr2.addStatus("trd", null, 0, null, 0, 0);
					
					// Link partners
					usr.getSession().getTrader().open(usr2.getSession());
					usr2.getSession().getTrader().open(usr.getSession());
					
					// Refresh partners
					usr.getSession().getTrader().refresh();
					usr2.getSession().getTrader().refresh();
				}
			}
		}
	}
}
