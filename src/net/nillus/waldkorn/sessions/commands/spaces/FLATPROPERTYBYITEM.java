package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;

public class FLATPROPERTYBYITEM extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse data
		String propType = msg.nextArgument('/');
		int itemID = Integer.parseInt(msg.nextArgument('/'));
		
		Item item = m_session.getSpaceSession().getInventory().getItem(itemID);
		if(item != null)
		{
			if(item.definition.behaviour.isDecoration)
			{
				if(m_session.getSpaceSession().getUser().isFlatController)
				{
					// Delete item from inventory
					m_session.getSpaceSession().getInventory().removeItem(itemID);
					m_session.getSpaceSession().getInventory().sendStrip("update");
					
					// Apply flat property
					Space flat = m_session.getSpaceSession().getSpace().getInfo();
					if(item.definition.sprite.equals("wallpaper"))
					{
						flat.wallpaper = Short.parseShort(item.customData);
					}
					else if(item.definition.sprite.equals("floor"))
					{
						flat.floor = Short.parseShort(item.customData);
					}
					else
					{
						return;
					}
					
					// Notify clients
					ServerMessage notify = new ServerMessage("FLATPROPERTY");
					notify.appendNewArgument(item.definition.sprite);
					notify.appendPartArgument(item.customData);
					m_session.getSpaceSession().getSpace().broadcast(notify);
					
					// Delete item and update flat info
					item.delete(m_session.getServer().getDatabase());
					flat.update(m_session.getServer().getDatabase());
				}
			}
		}
	}
}
