package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;

public class KEYENCRYPTED extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		//String keyEncrypted = msg.nextArgument();
		
		// You are fine bro, meep!
		client.cryptoOK();
	}
}
