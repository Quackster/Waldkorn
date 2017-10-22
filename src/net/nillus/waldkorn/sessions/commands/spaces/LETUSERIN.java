package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.MasterSession;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class LETUSERIN extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Does this user have the right to answer doorbells?
		if (!m_session.getSpaceSession().getUser().isFlatController)
		{
			m_session.getSpaceSession().systemMsg("Yo dude who do you think you are!");
			return;
		}
		
		// Get name of user to let in (this user was ringing the bell)
		String name = msg.nextArgument();
		MasterSession session = m_session.getServer().getSessionMgr().getMasterSessionOfUser(name);
		
		// User was ringing the bell?
		if(session != null && session.getSpaceSession() != null && session.getSpaceSession().waitingForFlatDoorbell)
		{
			// Authenticate ringer with this flat
			session.getSpaceSession().authenticatedFlat = m_session.getSpaceSession().getSpace().getInfo().ID;
			
			// Trigger ringer to enter
			session.send(new ServerMessage("FLAT_LETIN"));
		}
	}
}
