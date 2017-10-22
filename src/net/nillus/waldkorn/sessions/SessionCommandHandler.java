package net.nillus.waldkorn.sessions;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;

public abstract class SessionCommandHandler
{
	protected Session m_session;
	protected ServerMessage m_response;
	
	public void init(Session session, ServerMessage responseBuffer)
	{
		m_session = session;
		m_response = responseBuffer;
	}
	
	protected void send(ServerMessage msg)
	{
		m_session.send(msg);
	}
	
	protected void sendResponse()
	{
		m_session.send(m_response);
	}
	
	public abstract void handle(ClientMessage msg) throws Exception;
}
