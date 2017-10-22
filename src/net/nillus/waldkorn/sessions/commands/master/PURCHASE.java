package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.catalogue.CatalogueArticle;
import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class PURCHASE extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse receiver etc
		msg.advance(1);
		String articleID = msg.nextArgument();
		
		CatalogueArticle article = m_session.getServer().getCatalogue().getArticle(articleID);
		if(article == null || article.price > m_session.getUserObject().credits)
		{
			m_session.systemMsg("You cannot afford that catalogue article!");
		}
		else
		{
			// Get order info, client doesn't seem to send it now... luckily, we memorzed it!
			GETORDERINFO info = (GETORDERINFO)m_session.getCmdHandlerMgr().get("GETORDERINFO");
			
			// Purchase it
			Item[] shipping = m_session.getServer().getCatalogue().purchaseArticle(article.id, info.customData, m_session.getUserObject().ID, m_session.getUserObject().ID, false, null);
			if(shipping != null)
			{
				// Deduct credits
				m_session.getUserObject().credits -= article.price;
				m_session.getUserObject().updateValuables(m_session.getServer().getDatabase());
				
				// Kaching!
				m_response.set("WALLETBALANCE");
				m_response.appendNewArgument(Integer.toString(m_session.getUserObject().credits));
				sendResponse();
				
				// Add item to inventory
				for(Item item : shipping)
				{
					m_session.getSpaceSession().getInventory().addItem(item);
				}
				
				// Refresh inventory
				m_response.set("ADDSTRIPITEM");
				sendResponse();
			}
		}
	}
}
