package net.nillus.waldkorn.sessions;

import java.util.HashMap;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.ServerMessage;

public final class SessionCommandHandlerManager
{
	private Session m_session;
	private ServerMessage m_response;
	private HashMap<String, SessionCommandHandler> m_handlers;
	
	public SessionCommandHandlerManager(Session session)
	{
		m_session = session;
		m_response = new ServerMessage();
		m_handlers = new HashMap<String, SessionCommandHandler>();
	}
	
	public void setSession(MasterSession session)
	{
		m_session = session;
	}
	
	public void add(SessionCommandHandler handler)
	{
		this.add(handler.getClass().getSimpleName(), handler);
	}
	
	public void add(String command, SessionCommandHandler handler)
	{
		handler.init(m_session, m_response);
		m_handlers.remove(command);
		m_handlers.put(command, handler);
	}
	
	public void remove(String command)
	{
		m_handlers.remove(command);
	}
	
	public SessionCommandHandler get(String command)
	{
		return m_handlers.get(command);
	}
	
	public void clear()
	{
		m_handlers.clear();
	}
	
	public final boolean handleMessage(ClientMessage message)
	{
		SessionCommandHandler handler = this.get(message.getType());
		if(handler != null)
		{
			try
			{
				handler.handle(message);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public final void callHandler(String command)
	{
		SessionCommandHandler handler = this.get(command);
		if(handler != null)
		{
			try
			{
				handler.handle(null);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public Session getSession()
	{
		return m_session;
	}
}
