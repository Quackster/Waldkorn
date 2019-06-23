package net.nillus.waldkorn.sessions.commands.master;


import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class SEND_USERPASS_TO_EMAIL extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		String name = msg.nextArgument();
		String email = msg.nextArgument();
		m_session.systemMsg("Sorry, but this feature has not been implemented in the server yet.");
		m_session.getServer().getLogger().debug("SEND_USERPASS_TO_EMAIL", name + " (" + email + ")");
	}
}
