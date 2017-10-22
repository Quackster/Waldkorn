package net.nillus.waldkorn.sessions;

import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.NetworkConnection;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.commands.LOGIN;
import net.nillus.waldkorn.sessions.commands.VERSIONCHECK;
import net.nillus.waldkorn.spaces.SpaceSession;
import net.nillus.waldkorn.users.User;

public abstract class Session extends ServerComponent
{
	public final long sessionID;
	private NetworkConnection m_connection;
	private long m_lastMessageTime;
	private SessionCommandHandlerManager m_cmdHandlerMgr;
	
	// Hacky way of getting this all right... I know!
	private MasterSession m_master;
	private SpaceSession m_space;
	
	public Session(long sessionID, NetworkConnection connection)
	{
		super(connection.getServer());
		this.sessionID = sessionID;
		m_connection = connection;
		m_connection.startSession(this);
		m_cmdHandlerMgr = new SessionCommandHandlerManager(this);
	}
	
	public void start()
	{
		// Register default handlers for all sessions
		m_cmdHandlerMgr.add(new VERSIONCHECK());
		m_cmdHandlerMgr.add(new LOGIN());
		
		// Greet client
		this.send(new ServerMessage("HELLO"));
	}
	
	public void requestRemoval(String reason)
	{
		m_server.getSessionMgr().removeSession(this.sessionID, reason);
	}
	
	public void handleMessage(ClientMessage msg)
	{
		m_lastMessageTime = System.currentTimeMillis();
		
		// Log the incoming message
		m_server.getLogger().debug(this, "< " + msg.toString());
		
		// Try to handle it
		if(!m_cmdHandlerMgr.handleMessage(msg))
		{
			m_server.getLogger().debug(this, "no command handler for message " + msg.getType());
		}
	}
	
	public void send(ServerMessage msg)
	{
		// Log the outgoing message
		m_server.getLogger().debug(this, "> " + msg.toString());
		
		m_connection.send(msg);
	}
	
	public void systemMsg(String text)
	{
		ServerMessage msg = new ServerMessage("SYSTEMBROADCAST");
		msg.appendNewArgument(text);
		this.send(msg);
	}
	
	public void systemError(String text)
	{
		ServerMessage msg = new ServerMessage("ERROR");
		msg.appendArgument(text);
		this.send(msg);
	}
	
	public NetworkConnection getConnection()
	{
		return m_connection;
	}
	
	public void setConnection(NetworkConnection connection)
	{
		m_connection = connection;
		m_cmdHandlerMgr = new SessionCommandHandlerManager(this);
	}
	
	public SessionCommandHandlerManager getCmdHandlerMgr()
	{
		return m_cmdHandlerMgr;
	}
	
	public MasterSession getMasterSession()
	{
		return m_master;
	}
	
	public void setMasterSession(MasterSession session)
	{
		m_master = session;
	}
	
	public User getUserObject()
	{
		return m_master.getUserObject();
	}
	
	public SpaceSession getSpaceSession()
	{
		return m_space;
	}
	
	public void setSpaceSession(SpaceSession session)
	{
		m_space = session;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName() + " #" + this.sessionID;
	}
}
