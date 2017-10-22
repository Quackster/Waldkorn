package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.ItemTrader;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class TRADE_UNACCEPT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Trading?
		ItemTrader trader = m_session.getSpaceSession().getTrader();
		if(trader.busy())
		{
			// Unaccept!
			trader.unaccept();
			
			// Refresh clients!
			trader.refresh();
			trader.getPartner().refresh();
		}
	}
}
