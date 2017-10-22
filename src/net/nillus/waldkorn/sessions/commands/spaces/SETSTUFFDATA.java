package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class SETSTUFFDATA extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		int itemID = Integer.parseInt(msg.nextArgument('/'));
		String dataClass = msg.nextArgument('/');
		String data = msg.nextArgument('/');
		
		m_session.getSpaceSession().getSpace().getInteractor().updateActiveObjectData(m_session.getUserObject().ID, itemID, dataClass, data);
	}
}
