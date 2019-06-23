package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.Waldkorn;
import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceInstanceInteractor;
import net.nillus.waldkorn.spaces.SpaceUser;

public class DICE_OFF extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse ID
		int itemID = Integer.parseInt(msg.nextArgument('/'));
		
		// Get the object
		Item obj = m_session.getSpaceSession().getSpace().getInteractor().getActiveObject(itemID);
		if(obj != null && obj.definition.behaviour.isDice)
		{
			// Is user standing next to the object?
			SpaceUser usr = m_session.getSpaceSession().getSpace().getUserByUserID(m_session.getUserObject().ID);

			if(usr != null && SpaceInstanceInteractor.mapTilesTouch(obj.X, obj.Y, usr.X, usr.Y))
			{
				// Notify clients
				ServerMessage notify = new ServerMessage("DICE_VALUE");
				notify.appendArgument(Integer.toString(itemID));
				notify.appendArgument(Integer.toString(itemID * 38));
				m_session.getSpaceSession().getSpace().broadcast(notify);
				
				// Save object (dataclass: VALUE)
				obj.customData = "0";
				obj.update(Waldkorn.getServer().getDatabase());
			}
		}
	}
}
