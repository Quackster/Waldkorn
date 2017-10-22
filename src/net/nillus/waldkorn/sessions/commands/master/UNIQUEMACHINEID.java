package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class UNIQUEMACHINEID extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		String machineID = msg.nextArgument();
	}
}
