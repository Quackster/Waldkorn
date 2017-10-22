package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceUser;

public class JUMPPERF extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get user and object on tile
		SpaceUser usr = m_session.getSpaceSession().getUser();
		Item obj = m_session.getSpaceSession().getSpace().getInteractor().getPassiveObjectOnTile(usr.X, usr.Y);
		
		// User is indeed diving?
		if (usr != null && obj != null && obj.definition.sprite.equals("poolLift"))
		{
			// Parse dive data
			String name = msg.nextArgument((char)13);
			String figure = msg.nextArgument((char)13);
			String poolFigure = msg.nextArgument((char)13);
			String data = msg.nextArgument((char)13);
			
			// Start replay for diving user, show user on TV for other clients
			ServerMessage notify = new ServerMessage("JUMPDATA");
			notify.appendNewArgument(m_session.getUserObject().name);
			notify.appendNewArgument(data);
			m_session.getSpaceSession().getSpace().broadcast(notify);
		}
	}
}
