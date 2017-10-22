package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.ItemTrader;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class TRADE_ACCEPT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		ItemTrader me = m_session.getSpaceSession().getTrader();
		if(me.busy())
		{
			me.accept();
			me.refresh();
			me.getPartner().refresh();
			
			// Partner has accepted aswell?
			if(me.getPartner().accepting())
			{
				// Swap offers
				me.castOfferAway();
				me.getPartner().castOfferAway();
				
				// Meep!
				me.abort();
			}
		}
	}
}
