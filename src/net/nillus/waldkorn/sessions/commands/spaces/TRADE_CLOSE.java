package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class TRADE_CLOSE extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		m_session.getSpaceSession().getTrader().abort();
	}
}
