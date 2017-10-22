package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceTile;
import net.nillus.waldkorn.spaces.SpaceUser;

public class SPLASH_POSITION extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get user and tile object
		SpaceUser usr = m_session.getSpaceSession().getUser();
		Item obj = m_session.getSpaceSession().getSpace().getInteractor().getPassiveObjectOnTile(usr.X, usr.Y);
		
		// User is indeed diving?
		if (usr != null && obj != null && obj.definition.sprite.equals("poolLift"))
		{
			// Determine 'landing position' of jump and position of pool exit
			SpaceTile position = SpaceTile.parse(msg.getBody());
			SpaceTile exit = SpaceTile.parse(obj.customData);
			
			// Display splash for clients
			ServerMessage notify = new ServerMessage("SHOWPROGRAM");
			notify.appendArgument("BIGSPLASH");
			notify.appendArgument("POSITION");
			notify.appendArgument(position.toString());
			m_session.getSpaceSession().getSpace().broadcast(notify);
			
			// Locate user in pool on landing position
			usr.addStatus("swim", null, 0, null, 0, 0);
			m_session.getSpaceSession().getSpace().getInteractor().warpUser(usr, position.X, position.Y, true);
			
			// Start moving to pool exit
			m_session.getSpaceSession().getSpace().getInteractor().startUserMovement(usr, exit.X, exit.Y, false);
			
			// Door is open again!
			m_session.getSpaceSession().getSpace().showProgram(obj.itemData, "open");
		}
	}
}
