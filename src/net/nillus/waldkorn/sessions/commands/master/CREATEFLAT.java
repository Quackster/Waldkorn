package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;
import net.nillus.waldkorn.util.SecurityUtil;

public class CREATEFLAT  extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// CREATEFLAT /first floor/My room/model_b/open/1"
		String floor = msg.nextArgument('/');
		if(!floor.equals("first floor"))
		{
			return;
		}

		// Gather data
		Space flat = new Space();
		flat.name = SecurityUtil.filterInput(msg.nextArgument('/'));
		flat.model = msg.nextArgument('/');
		flat.accessType = msg.nextArgument('/');
		flat.showOwner = (msg.nextArgument('/').equals("1"));
				
		// Create flat
		flat.ownerID = m_session.getUserObject().ID;
		if (m_session.getServer().getSpaceAdmin().createFlat(flat))
		{
			m_response.set("FLATCREATED");
			m_response.appendNewArgument(Integer.toString(flat.ID));
			m_response.appendNewArgument(flat.name);
			m_response.appendNewArgument("habbohotel.kicks-ass.net");
			m_response.appendNewArgument("1337");
			sendResponse();
			m_session.systemMsg("Please go to your room via the navigators 'Own Rooms'");
		}
		else
		{
			m_session.systemError("Could not create your flat for whatever reason!\rTry again or contact administrator.");
		}
	}
}
