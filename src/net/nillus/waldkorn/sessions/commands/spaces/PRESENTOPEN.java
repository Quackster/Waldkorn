package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;

public class PRESENTOPEN extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		if (client.getSpaceInstance().getUserByUserID(client.sessionID).isFlatAdmin)
		{
			// Get present box item
			int itemID = Integer.parseInt(msg.nextArgument('/'));
			Item box = client.getSpaceInstance().getInteractor().getActiveObject(itemID);
			
			// Present box?
			if (box != null && box.definition.sprite.startsWith("present_gen"))
			{
				// Pickup present box from room
				client.getSpaceInstance().getInteractor().pickupActiveObject(itemID);
				HabboHotel.getItemAdmin().deleteItem(box);
				
				// Parse article data
				int articleID = 0;
				String articleCustomData = null;
				try
				{
					String[] articleData = box.customData.split(":", 3);
					articleID = Integer.parseInt(articleData[0]);
					if(!articleData[1].equals("*"))
					{
						articleCustomData = articleData[1];
					}
				}
				catch (Exception ex)
				{
					Log.error("PRESENTOPEN: article data parser error for string \"" + box.customData + "\", present deleted.");
				}
				
				// Purchase articles
				Item[] content = HabboHotel.getCatalogue().purchaseArticle(articleID, articleCustomData, client.getUserObject().ID, client.getUserObject().ID, false, null);
				if (content == null)
				{
					client.systemMsg("Sorry, but the contents of this present box are not valid (anymore)!\rIf this happens frequently with the same kind of product, then please contact administrator.");
				}
				else
				{
					// Add content to inventory
					for (Item item : content)
					{
						client.getInventory().addItem(item);
					}
					
					// Refresh inventory
					client.getInventory().sendStrip("last");
					
					// Build response
					m_response.set("PRESENTOPEN");
					m_response.appendNewArgument(content[0].definition.sprite);
					if (content[0].definition.sprite.equals("poster"))
					{
						m_response.appendNewArgument("poster " + content[0].customData);
					}
					else
					{
						m_response.appendNewArgument(content[0].definition.sprite);
						if (content[0].definition.behaviour.onFloor)
						{
							m_response.appendNewArgument("");
							m_response.appendArgument(Byte.toString(content[0].definition.length), '|');
							m_response.appendArgument(Byte.toString(content[0].definition.width), '|');
							m_response.appendArgument(content[0].definition.color, '|');
						}
					}
					sendResponse();
				}
			}
		}
	}
}
