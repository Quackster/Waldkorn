package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class ADDITEM extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		/*
		 * Note from Nillus:
		 * 
		 * Alright, appearantly Aapo didn't see the security issue with post.its back then.
		 * It worked like this:
		 * 1) Client tells server to place down a post.it with sprite A, at position B, with color C and text D... without specifying the item ID of the pad
		 * 2) Client tells server to deduct 1 post.it from the pad with item id X
		 * 
		 * This could allow people to create free post.its, just send the data for placing the post.it and don't send the deduct data
		 * Anyway, I decided to ignore SETSTRIPITEMDATA and just do it like this, it's secure.
		 */
		
		// Can only place post.it when having rights!
		if(!m_session.getSpaceSession().getUser().isFlatController)
		{
			return;
		}
		
		// Parse input data
		String sprite = msg.nextArgument('/');
		String position = msg.nextArgument('/');
		String color = msg.nextArgument(' ');
		String text = msg.getRemainingBody();
		
		// Get post.it pad from inventory
		Item pad = m_session.getSpaceSession().getInventory().getItemWithSprite(sprite);
		if(pad == null)
		{
			return;
		}
		
		// Create new post.it from the pad
		Item postit = new Item();
		postit.definition = pad.definition;
		postit.customData = color + " " + text;
		if(!m_session.getServer().getItemAdmin().storeItem(postit))
		{
			return;
		}
		
		// Calculate the new pad size or delete pad if it's empty now
		int padSize = Integer.parseInt(pad.customData) - 1;
		if(padSize > 0)
		{
			pad.customData = Integer.toString(padSize);
			pad.update(m_session.getServer().getDatabase());
		}
		else
		{
			m_session.getSpaceSession().getInventory().removeItem(pad.ID);
			pad.delete(m_session.getServer().getDatabase());
		}
		
		// Place the new post.it in the room
		m_session.getSpaceSession().getSpace().getInteractor().placeWallItem(postit, position);
	}
}
