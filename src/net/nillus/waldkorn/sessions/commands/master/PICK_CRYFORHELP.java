package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.moderation.ModerationCenter;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;

public class PICK_CRYFORHELP extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse call ID from chatlog URL
		int callID = ModerationCenter.parseCallID(msg.getBody());
		
		// Valid chatlog URL?
		if(callID != -1)
		{
			// Not picked up already?
			if(!HabboHotel.getModerationCenter().pickCallForHelp(callID, client.getUserObject().name))
			{
				client.systemMsg("This call has already been picked up!");
			}
		}
	}
}
