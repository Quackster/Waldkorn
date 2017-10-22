package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class SETITEMDATA extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Can only modify post.it when having rights!
		if(!m_session.getSpaceSession().getUser().isFlatController)
		{
			return;
		}
		
		// Parse the new data
		int itemID = Integer.parseInt(msg.nextArgument('/'));
		String color = msg.nextArgument(' ');
		String text = msg.getRemainingBody();
		Item postit = m_session.getSpaceSession().getSpace().getInteractor().getWallItem(itemID);
		if(postit == null || !postit.definition.behaviour.isPostIt)
		{
			return;
		}
		
		// Apply the new data
		postit.customData = color + " " + text;
		postit.update(m_session.getServer().getDatabase());
		
		// Refresh the item
		ServerMessage notify = new ServerMessage("ITEMS");
		notify.appendObject(postit);
		m_session.getSpaceSession().getSpace().broadcast(notify);
	}
}
