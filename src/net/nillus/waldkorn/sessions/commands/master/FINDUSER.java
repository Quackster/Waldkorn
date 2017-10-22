package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.users.User;

public class FINDUSER extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get name from message
		String name = msg.nextArgument();
		
		// Get requested user from register
		User usr = m_session.getServer().getUserRegister().getUserInfo(name);
		
		// User exists?
		if (usr == null)
		{
			m_response.set("NOSUCHUSER");
		}
		else
		{
			m_response.set("MEMBERINFO");
			m_response.appendArgument("MESSENGER");
			m_response.appendNewArgument(usr.name);
			m_response.appendNewArgument(usr.messengerMotto);
			m_response.appendNewArgument(usr.lastActivity.toString());
			m_response.appendNewArgument("unknown");
			m_response.appendNewArgument(usr.figure);
		}
		
		// Send the response
		sendResponse();
	}
}
