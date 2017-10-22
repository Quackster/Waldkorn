package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;

public class GETAVAILABLESETS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// This is used for Figure and such. Basically the "sets" that are available to the client.
		//String clientSets = HabboHotel.getPropBox().get("client.sets");
		String clientSets = new String();
		
		// Logged in?
		if (client.getUserObject() == null)
		{
			clientSets = HabboHotel.getPropBox().get("client.sets");
		}
		else
		{
			// Is this User subscribed to HC or not)
			if (client.getUserObject().isHC() == false)
			{
				clientSets = HabboHotel.getPropBox().get("client.sets");
			}
			else
			{
				clientSets = HabboHotel.getPropBox().get("client.hc.sets");
			}
		}
		
		// We don't want sets to be nothing...
		if (clientSets != null)
		{
			m_response.set("AVAILABLESETS");
			m_response.appendNewArgument(clientSets);
			sendResponse();
		}
		else
		{
			client.systemMsg("The server was unable to locate the appropriate figuresets!\rContact administrator.");
		}
	}
}
