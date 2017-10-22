package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.SpaceInstance;

public class TRYFLAT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse flat data & entry password (optional)
		int spaceID = Integer.parseInt(msg.nextArgument('/'));
		String password = msg.nextArgument('/');
		
		// Attempt to get an instance of the flat
		SpaceInstance instance = m_session.getServer().getSpaceServer().getFlatInstance(spaceID, true);
		if(instance == null)
		{
			m_session.requestRemoval("cannot create instance of flat " + spaceID);
			return;
		}
		
		// Override all access checks if flat owner
		if(m_session.getUserObject().ID != instance.getInfo().ownerID)
		{
			// Override all access checks if can_enter_any_room
			if(!m_session.getUserObject().hasRight("can_enter_any_room"))
			{
				if(instance.getInfo().accessType.equals("password"))
				{
					// Bad password?
					if(!instance.getInfo().password.equals(password))
					{
						m_session.systemError("Incorrect flat password");
						return;
					}
				}
				else if(instance.getInfo().accessType.equals("closed"))
				{
				// Possible doorbell response?
					if(instance.ringDoorbell(m_session.getUserObject().name))
					{
						m_session.getSpaceSession().waitingForFlatDoorbell = true;
					}
				// Wait for eventual response (client = standing by)
					return;
				}
			}
		}
		
		// Client is allowed to enter now (either no password required, or correct password given)
		m_session.getSpaceSession().authenticatedFlat = spaceID;
			
		// Tell client that it can enter now (GOTOFLAT)
		m_response.set("FLAT_LETIN");
		sendResponse();
	}
}
