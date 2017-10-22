package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.catalogue.CatalogueArticle;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class GETORDERINFO extends SessionCommandHandler
{
	public String articleID = null;
	public String customData = null;
	
	public void handle(ClientMessage msg)
	{
		// Parse info on the article to get info on
		msg.advance(1);
		String section = msg.nextArgument();
		String articleID = msg.nextArgument();
		String extra = msg.nextArgument();
		
		// Get the article
		CatalogueArticle article = m_session.getServer().getCatalogue().getArticle(articleID);
		if(article == null || !article.section.equals(section))
		{
			if(articleID == "deal")
			{
				m_session.getServer().getLogger().info("Catalogue", "Article Page: " + section + " arcticleID: " + articleID + " " + extra);
			}
			else
			{
				m_session.getServer().getLogger().info("Catalogue", "Article Page: " + section + " arcticleID: " + articleID);
			}
			m_session.systemMsg("Invalid catalogue article!");
		}
		else
		{
			// Memorize the order info
			this.articleID = article.id;
			if(article.getItem().behaviour.isDecoration || article.getItem().sprite.equals("poster"))
			{
				this.customData = Integer.toString(Integer.parseInt(extra));
			}
			else
			{
				this.customData = null;
			}
			
			m_response.set("ORDERINFO");
			m_response.appendNewArgument(article.id);
			m_response.appendNewArgument(Integer.toString(article.price));
			m_response.appendNewArgument(customData);
			m_response.appendNewArgument(article.getItem().name);
			sendResponse();
		}
	}
}
