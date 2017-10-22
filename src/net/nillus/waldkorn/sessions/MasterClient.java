package net.nillus.waldkorn.sessions;

import java.net.Socket;
import java.util.Vector;

import net.nillus.waldkorn.Server;
import net.nillus.waldkorn.access.UserAccessEntry;
import net.nillus.waldkorn.moderation.CallForHelp;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.commands.INFORETRIEVE;
import net.nillus.waldkorn.sessions.commands.LOGIN;
import net.nillus.waldkorn.sessions.commands.VERSIONCHECK;
import net.nillus.waldkorn.sessions.commands.master.UPDATE;

public final class MasterClient extends MasterSession
{
	private MasterServer m_masterServer;
	private UserAccessEntry m_userAccessEntry;
	private SessionCommandHandlerManager<MasterClient> m_commandHandlerMgr;
	
	public MasterClient(long sessionID, Socket socket, MasterServer server) throws Exception
	{
		super(sessionID, socket);
		m_masterServer = server;
		m_commandHandlerMgr = new SessionCommandHandlerManager<MasterClient>(this);
		
		// Register tuff
		m_commandHandlerMgr.install(new VERSIONCHECK());
		m_commandHandlerMgr.install(new LOGIN());
		m_commandHandlerMgr.install(new INFORETRIEVE());
		m_commandHandlerMgr.install(new UPDATE());
	}
	

	@Override
	protected boolean handleIncomingMessage(ClientMessage msg)
	{
		return m_commandHandlerMgr.handleMessage(msg);
	}
	
	@Override
	protected void cleanupSession()
	{
		if(super.getUserObject() != null)
		{
			// Bla bla broadcast logout on messenger
			
			// Update user object
			
			// Log logout
			getServer().getAccessControl().logLogout(m_userAccessEntry);
		}
	}
	
	@Override
	protected void loginOk()
	{
		// Create access log entry
		
		// Install handlers for user management
		
		// Install navigator handlers
		
		// Perform logic for moderators
		if (getUserObject().ID == 327727277)//getUserObject().hasRight("can_answer_cfh"))
		{
			// Register moderation handlers
			//m_commandHandlerMgr.install(new PICK_CRYFORHELP());
			
			// Send all the pending calls (while this moderator was offline)
			Vector<CallForHelp> pendingCalls = m_masterServer.getModerationCenter().getPendingCalls();
			for (CallForHelp call : pendingCalls)
			{
				response.set("CRYFORHELP");
				response.appendObject(call);
				sendResponse();
			}
		}
		
		// Send message of the day
		if (m_masterServer.getAccessControl().getMessageOfTheDay() != null)
		{
			this.systemMsg("Message Of The Day:\r" + m_masterServer.getAccessControl().getMessageOfTheDay());
		}
			
		// Log this event
		getServer().getLogger().info(this, "user #" + getUserObject().ID + " [" + getUserObject().name + "] logged in from client #" + this.sessionID + " [" + getIpAddress() + "]");
	}

	@Override
	public Server getServer()
	{
		return m_masterServer;
	}
	
	public MasterServer getMasterServer()
	{
		return m_masterServer;
	}
	
	public SessionCommandHandlerManager<MasterClient> getCommandHandlerMgr()
	{
		return m_commandHandlerMgr;
	}
	
	public UserAccessEntry getUserAccessEntry()
	{
		return m_userAccessEntry;
	}
}
