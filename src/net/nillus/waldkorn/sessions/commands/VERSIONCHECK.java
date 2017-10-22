package net.nillus.waldkorn.sessions.commands;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class VERSIONCHECK extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Verify client version
		String clientVersion = msg.nextArgument();
		if(!clientVersion.equals("client002"))
		{
			m_session.requestRemoval("invalid client version '" + clientVersion + "'");	
		}
		else
		{
            m_response.set("ENCRYPTION_OFF");
            sendResponse();
            
            m_response.set("SECRET_KEY");
            m_response.appendArgument("");
            sendResponse();
		}
	}
}
