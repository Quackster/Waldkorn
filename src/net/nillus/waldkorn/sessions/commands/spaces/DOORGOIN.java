package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class DOORGOIN extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		int itemID = Integer.parseInt(msg.nextArgument('/'));
	}
}
