package net.nillus.waldkorn.catalogue;

import java.sql.ResultSet;
import java.util.HashMap;

import net.nillus.waldkorn.Server;
import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.items.Item;
/**
 * The Catalogue is an ingame 'catalogue' consisting out of pages with articles (such as furniture) on them.\r
 * Articles can be purchased by Users at the expense of credits.
 * 
 * @author Nillus
 */
public class Catalogue extends ServerComponent
{
	private HashMap<String, CatalogueArticle> m_articles;
	
	public Catalogue(Server server)
	{
		super(server);
		m_articles = new HashMap<String, CatalogueArticle>();
	}
	
	public int loadArticles()
	{
		m_articles.clear();
		
		// Parse new values from database
		ResultSet result = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT * FROM catalogue_articles ORDER BY id ASC;");
			while (result.next())
			{
				CatalogueArticle article = new CatalogueArticle();
				article.id = result.getString("id");
				article.section = result.getString("section");
				article.price = result.getInt("price");
				article.setInnerItem(m_server.getItemAdmin().getDefinition(result.getInt("definitionid")));
				m_articles.put(article.id, article);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		
		// Log how many loaded
		m_server.getLogger().info("Catalogue", "loaded " + m_articles.size() + " articles");
		return m_articles.size();
	}
	
	public Item[] purchaseArticle(String articleID, String customData, int userID, int receiverID, boolean isGift, String giftNote)
	{
		CatalogueArticle article = this.getArticle(articleID);
		if (article != null)
		{
			// Create item
			Item item = new Item();
			Item[] shipping = null;
			
			// For this user!
			item.ownerID = receiverID;
			
			// Determine actual item to ship (presentbox or article item)
			if (isGift)
			{
				// Create presentbox item
				item.definition = m_server.getItemAdmin().getPresentBoxDefinition();
				if (item.definition == null)
				{
					m_server.getLogger().error("Catalogue", "could not locate a present box item definition!", null);
					return null;
				}
				else
				{
					// Apply gift note
					item.customData = articleID + ":" + ((customData == null) ? "*" : customData) + "::" + giftNote;
				}
			}
			else
			{
				// Get articles item def
				item.definition = article.getItem();
				
				// Apply extra stuff
				if (item.definition.behaviour.isPostIt)
				{
					item.customData = "20";
				}
				else if (customData != null)
				{
					item.customData = customData;
				}
				else if(!item.definition.dataClass.equals("NULL"))
				{
					item.customData = "FALSE";
				}
			}
			
			// Store item
			if (m_server.getItemAdmin().storeItem(item))
			{
				// Handle optional actions if not present box
				if (!isGift)
				{
					// Teleporter? (shipped in linking pairs)
					if (item.definition.behaviour.isTeleporter)
					{
						// Create linking teleporter
						Item item2 = new Item();
						item2.definition = item.definition;
						item2.ownerID = receiverID;
						item2.teleporterID = item.ID; // Link to item1
						item2.customData = "FALSE";
						
						// Store item2
						if (m_server.getItemAdmin().storeItem(item2))
						{
							item.teleporterID = item2.ID; // Link to item2
							item.update(m_server.getDatabase());
							
							shipping = new Item[2];
							shipping[1] = item2;
						}
					}
				}
				
				// Add 'primary item' to shipping
				if (shipping == null)
				{
					shipping = new Item[1];
				}
				shipping[0] = item;
				
				// There it goes!
				return shipping;
			}
		}
		
		// Failed for whatever reason
		m_server.getLogger().error("Catalogue", "purchase for article #" + articleID + " (gift: " + isGift + ") failed. Please consult error log.", null);
		return null;
	}
	
	public CatalogueArticle getArticle(String articleID)
	{
		return m_articles.get(articleID);
	}
	
	/**
	 * Returns the total amount of articles in the Catalogue.
	 */
	public int articleAmount()
	{
		return m_articles.size();
	}
}
