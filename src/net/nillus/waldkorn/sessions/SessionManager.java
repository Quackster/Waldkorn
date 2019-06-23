package net.nillus.waldkorn.sessions;

import java.util.Collection;
import java.util.HashMap;

import net.nillus.waldkorn.MasterServer;
import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.net.NetworkConnection;
import net.nillus.waldkorn.spaces.SpaceSession;
import net.nillus.waldkorn.users.User;

public class SessionManager extends ServerComponent
{
	private Object m_lock;
	
	private HashMap<Long, MasterSession> m_masterSessions;
	private HashMap<Long, SpaceSession> m_spaceSessions;
	private HashMap<Integer, MasterSession> m_masterSessionsByUserId;
	
	public SessionManager(MasterServer server)
	{
		super(server);
		m_lock = new Object();
		m_masterSessions = new HashMap<Long, MasterSession>();
		m_spaceSessions = new HashMap<Long, SpaceSession>();
		m_masterSessionsByUserId = new HashMap<Integer, MasterSession>();
	}
	
	public long generateSessionID()
	{
		return Math.abs(m_server.getRandom().nextLong());
	}
	
	public void addMasterConnection(NetworkConnection connection)
	{
		MasterSession session = new MasterSession(generateSessionID(), connection);
		session.setMasterSession(session);
		synchronized(m_lock)
		{
			m_masterSessions.put(session.sessionID, session);
		}
		
		m_server.getLogger().info(this, "accepted " + session + " from " + session.getConnection().getIpAddress());
		
		// Greet client and register handlers

		session.start();
	}
	
	public void addSpaceConnection(NetworkConnection connection)
	{
		SpaceSession session = new SpaceSession(generateSessionID(), connection);
		session.setSpaceSession(session);
		synchronized(m_lock)
		{
			m_spaceSessions.put(session.sessionID, session);
		}
		
		// Greet client and register handlers
		session.start();
	}
	
	public boolean removeSession(long sessionID, String reason)
	{
		m_server.getLogger().info(this, "removing session #" + sessionID + " [" + reason + "]");
		
		boolean removed = false;
		synchronized(m_lock)
		{
			// Look for SpaceSessions first, they appear/die more than the MasterSessions
			SpaceSession spSession = m_spaceSessions.remove(sessionID);
			if(spSession != null)
			{
				spSession.exit(null);
				if(spSession.getMasterSession() != null)
				{
					spSession.getMasterSession().setSpaceSession(null);
				}
				removed = true;
			}
			
			// Well, it must be a MasterSession then!
			MasterSession maSession = m_masterSessions.remove(sessionID);
			if(maSession != null)
			{
				maSession.getConnection().disconnect();
				
				// Remove the SpaceSession aswell, we are not going to trust the client on this...
				if(maSession.getSpaceSession() != null)
				{
					maSession.getSpaceSession().exit(null);
				}
				removed = true;
			}
		}
		
		return removed;
	}
	
	public void processUserLogin(MasterSession session, User user)
	{
		// Kill concurrent session (if running)
		MasterSession concurrent = this.getMasterSessionOfUser(user.ID);
		if(concurrent != null)
		{
			concurrent.requestRemoval("concurrent login");
		}
		
		// Add to the user index
		synchronized(m_lock)
		{
			m_masterSessionsByUserId.put(user.ID, session);
		}
		m_server.getLogger().info(this, "user #" + user.ID + " [" + user.name + "] logged in");
	}
	
	public MasterSession getMasterSession(long sessionID)
	{
		synchronized(m_lock)
		{
			return m_masterSessions.get(sessionID);
		}
	}
	
	public MasterSession getMasterSessionOfUser(int userID)
	{
		synchronized(m_lock)
		{
			return m_masterSessionsByUserId.get(userID);
		}
	}
	
	public MasterSession getMasterSessionOfUser(String name)
	{
		synchronized(m_lock)
		{
			for(MasterSession session : m_masterSessionsByUserId.values())
			{
				if(session.getUserObject().name.equalsIgnoreCase(name))
				{
					return session;
				}
			}
			
			return null;
		}
	}
	
	public MasterSession getSpaceSessionSession(long sessionID)
	{
		synchronized(m_lock)
		{
			return m_masterSessions.get(sessionID);
		}
	}
	
	public int count()
	{
		return m_masterSessions.size();
	}
	
	public int userCount()
	{
		return m_masterSessionsByUserId.size();
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
	
	public Collection<MasterSession> getLoggedInUsers()
	{
		return m_masterSessionsByUserId.values();
	}
	
	/*
	 * 	public int dropTimedOutClients()
	{
		// Determine current time
		long nowTime = System.currentTimeMillis();
		
		// Gather the timed out clients
		Vector<MasterSession> selection = new Vector<MasterSession>();
		synchronized (m_sessions)
		{
			for (MasterSession session : m_sessions.values())
			{
				// Has this client timed out?
				if ((nowTime - client.getLastMessageTime()) > (120 * 1000))
				{
					selection.add(client);
				}
			}
		}
		
		// Disconnect the timed out clients
		for (MasterSession session : selection)
		{
			client.stop("timed out");
		}
		
		return selection.size();
	}*/
}
