package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class Sign extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get ID of sign
		int num = Integer.parseInt(msg.getBody());
		if (num >= 1 && num <= 14)
		{
			// Diving score: 4 ... 10 [1 = 4, 7 = 10]
			if(num >= 1 && num <= 7)
			{
				// TODO: account 'vote on dive'
			}
	
			// Add status
			SpaceUser usr = m_session.getSpaceSession().getUser();
			usr.removeStatus("dance");
			usr.addStatus("sign", Integer.toString(num), 2, null, 0, 0);
		}
	}
}
