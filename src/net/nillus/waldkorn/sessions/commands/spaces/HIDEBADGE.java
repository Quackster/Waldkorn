package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;

public class HIDEBADGE extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		client.getSpaceInstance().getUserByUserID(client.sessionID).removeStatus("mod");
	}
}
