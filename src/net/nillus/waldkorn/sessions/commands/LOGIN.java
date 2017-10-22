package net.nillus.waldkorn.sessions.commands;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterSession;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.sessions.commands.spaces.GOTOFLAT;
import net.nillus.waldkorn.sessions.commands.spaces.TRYFLAT;
import net.nillus.waldkorn.spaces.SpaceInstance;
import net.nillus.waldkorn.spaces.SpaceSession;
import net.nillus.waldkorn.users.User;

public class LOGIN extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Gather credentials
		String name = msg.nextArgument();
		String password = msg.nextArgument();
		
		// Validate credentials
		User usr = m_session.getServer().getUserRegister().getUserInfo(name);
		if (usr == null || !usr.password.equals(password))
		{
			m_session.systemError("login incorrect: Wrong password");
		}
		else
		{
			// New master login, or logging in to a space?
			if(m_session instanceof MasterSession)
			{
				m_session.getMasterSession().loginOk(usr);
				m_session.getServer().getSessionMgr().processUserLogin((MasterSession)m_session, usr);
			}
			else if(m_session instanceof SpaceSession)
			{
				// Can only do this when there's a master session
				MasterSession master = m_session.getServer().getSessionMgr().getMasterSessionOfUser(usr.ID);
				if(master == null)
				{
					m_session.requestRemoval("SpaceSession LOGIN without MasterSession");
					return;
				}
				
				// Link the sessions
				master.setSpaceSession((SpaceSession)m_session);
				m_session.setMasterSession((MasterSession)master);
				
				// Login to public space, or to flat?
				if(msg.nextArgument().equals("0"))
				{
					// Get public space on this port
					int portNumber = m_session.getConnection().getLocalPort();
					SpaceInstance instance = m_session.getServer().getSpaceServer().getUnitInstance(portNumber);
					if(instance == null)
					{
						m_session.requestRemoval("no unit running at TCP port" + portNumber);
						return;
					}
					
					// Enter the public space
					m_session.getSpaceSession().enter(instance);
				}
				else
				{
					m_session.getCmdHandlerMgr().add(new TRYFLAT());
					m_session.getCmdHandlerMgr().add(new GOTOFLAT());
				}
			}
			
			// Remove login handler (can only do 1 successful login)
			m_session.getCmdHandlerMgr().remove("REGISTER");
			m_session.getCmdHandlerMgr().remove("LOGIN");
		}
	}
}
