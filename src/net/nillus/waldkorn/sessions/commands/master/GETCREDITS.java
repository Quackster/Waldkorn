package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class GETCREDITS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		m_response.set("WALLETBALANCE");
		m_response.appendNewArgument(Integer.toString(m_session.getUserObject().credits));
		sendResponse();
	}
}
