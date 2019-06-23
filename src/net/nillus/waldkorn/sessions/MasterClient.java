package net.nillus.waldkorn.sessions;

import java.util.Vector;

import net.nillus.waldkorn.MasterServer;
import net.nillus.waldkorn.Waldkorn;
import net.nillus.waldkorn.access.UserAccessEntry;
import net.nillus.waldkorn.moderation.CallForHelp;
import net.nillus.waldkorn.moderation.ModerationCenter;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.net.NetworkConnection;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.commands.INFORETRIEVE;
import net.nillus.waldkorn.sessions.commands.LOGIN;
import net.nillus.waldkorn.sessions.commands.VERSIONCHECK;
import net.nillus.waldkorn.sessions.commands.master.UPDATE;

public final class MasterClient extends MasterSession
{
	private MasterServer m_masterServer;
	private UserAccessEntry m_userAccessEntry;
	private SessionCommandHandlerManager<MasterClient> m_commandHandlerMgr;
	
	public MasterClient(long sessionID, NetworkConnection socket, MasterServer server) throws Exception
	{
		super(sessionID, socket);
		m_masterServer = server;
		m_commandHandlerMgr = new SessionCommandHandlerManager<MasterClient>(this);
		
		// Register tuff
		m_commandHandlerMgr.add(new VERSIONCHECK());
		m_commandHandlerMgr.add(new LOGIN());
		m_commandHandlerMgr.add(new INFORETRIEVE());
		m_commandHandlerMgr.add(new UPDATE());
	}
	

	protected boolean handleIncomingMessage(ClientMessage msg)
	{
		return m_commandHandlerMgr.handleMessage(msg);
	}

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
			Vector<CallForHelp> pendingCalls = Waldkorn.getServer().getModerationCenter().getPendingCalls();
			for (CallForHelp call : pendingCalls)
			{
				ServerMessage response = new ServerMessage();
				response.set("CRYFORHELP");
				response.appendObject(call);
				//sendResponse();
			}
		}
		
		// Send message of the day
		if (m_masterServer.getAccessControl().getMessageOfTheDay() != null)
		{
			this.systemMsg("Message Of The Day:\r" + m_masterServer.getAccessControl().getMessageOfTheDay());
		}
			
		// Log this event
		getServer().getLogger().info(this, "user #" + getUserObject().ID + " [" + getUserObject().name + "] logged in from client #" + this.sessionID);//+ " [" + this.segetIpAddress() + "]");
	}

	@Override
	public MasterServer getServer()
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
