package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class CarryDrink extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		String item = msg.getBody();
		item = item.replace('/', '?');
			
		SpaceUser usr = m_session.getSpaceSession().getUser();
		usr.removeStatus("dance");
		usr.removeStatus("drink");
		usr.removeStatus("wave");
		usr.addStatus("carryd", item, 120, "drink", 12, 1);
	}
}
