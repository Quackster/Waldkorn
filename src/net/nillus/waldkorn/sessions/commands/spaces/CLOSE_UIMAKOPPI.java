package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceTile;
import net.nillus.waldkorn.spaces.SpaceUser;

public class CLOSE_UIMAKOPPI extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Client sends this when pool figure was updated while in clothing booth
		
		// Get user
		SpaceUser usr = m_session.getSpaceSession().getUser();
		Item obj = m_session.getSpaceSession().getSpace().getInteractor().getPassiveObjectOnTile(usr.X, usr.Y);
		
		// User in clothing booth?
		if(usr != null && obj != null && obj.definition.sprite.equals("poolBooth"))
		{
			// Re-send user to clients in space
			ServerMessage notify = new ServerMessage("USERS");
			notify.appendObject(usr);
			m_session.getSpaceSession().getSpace().broadcast(notify);
			
			// Open curtains
			m_session.getSpaceSession().getSpace().showProgram(obj.itemData, "open");
			
			// Move out of clothing booth
			usr.moveLock = false;
			SpaceTile goal = SpaceTile.parse(obj.customData);
			m_session.getSpaceSession().getSpace().getInteractor().startUserMovement(usr, goal.X, goal.Y, true);
		}
	}
}
