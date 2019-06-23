package net.nillus.waldkorn.sessions;

import net.nillus.waldkorn.access.UserAccessEntry;
import net.nillus.waldkorn.messenger.Messenger;
import net.nillus.waldkorn.net.NetworkConnection;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.commands.INFORETRIEVE;
import net.nillus.waldkorn.sessions.commands.master.ADD_FAVORITE_ROOM;
import net.nillus.waldkorn.sessions.commands.master.APPROVENAME;
import net.nillus.waldkorn.sessions.commands.master.CREATEFLAT;
import net.nillus.waldkorn.sessions.commands.master.CRYFORHELP;
import net.nillus.waldkorn.sessions.commands.master.DEL_FAVORITE_ROOM;
import net.nillus.waldkorn.sessions.commands.master.FINDUSER;
import net.nillus.waldkorn.sessions.commands.master.GETADFORME;
import net.nillus.waldkorn.sessions.commands.master.GETCREDITS;
import net.nillus.waldkorn.sessions.commands.master.GETDOORFLAT;
import net.nillus.waldkorn.sessions.commands.master.GETFLATINFO;
import net.nillus.waldkorn.sessions.commands.master.GETORDERINFO;
import net.nillus.waldkorn.sessions.commands.master.GETUNITUSERS;
import net.nillus.waldkorn.sessions.commands.master.GET_FAVORITE_ROOMS;
import net.nillus.waldkorn.sessions.commands.master.INITUNITLISTENER;
import net.nillus.waldkorn.sessions.commands.master.MESSENGERINIT;
import net.nillus.waldkorn.sessions.commands.master.PURCHASE;
import net.nillus.waldkorn.sessions.commands.master.REGISTER;
import net.nillus.waldkorn.sessions.commands.master.SEARCHBUSYFLATS;
import net.nillus.waldkorn.sessions.commands.master.SEARCHFLAT;
import net.nillus.waldkorn.sessions.commands.master.SEARCHFLATFORUSER;
import net.nillus.waldkorn.sessions.commands.master.SETFLATINFO;
import net.nillus.waldkorn.sessions.commands.master.UPDATE;
import net.nillus.waldkorn.sessions.commands.master.UPDATEFLAT;
import net.nillus.waldkorn.users.User;

public class MasterSession extends Session
{
	/**
	 * @author root
	 *
	 */

	private User m_userObject;
	private Messenger m_messenger;
	private UserAccessEntry m_accessEntry;
	
	public MasterSession(long sessionID, NetworkConnection connection)
	{
		super(sessionID, connection);
		
		this.getCmdHandlerMgr().add(new REGISTER());
		this.getCmdHandlerMgr().add(new FINDUSER());
		this.getCmdHandlerMgr().add(new APPROVENAME());
	}
	
	public void loginOk(User usr)
	{
		// Set the user object
		m_userObject = usr;
		
		// Core stuff
		this.getCmdHandlerMgr().add(new GETADFORME());
		this.getCmdHandlerMgr().add(new MESSENGERINIT());
		
		// User
		this.getCmdHandlerMgr().add(new UPDATE());
		this.getCmdHandlerMgr().add(new INFORETRIEVE());
		this.getCmdHandlerMgr().add(new GETCREDITS());
		
		// Navigator
		this.getCmdHandlerMgr().add(new INITUNITLISTENER());
		this.getCmdHandlerMgr().add(new GETUNITUSERS());
		this.getCmdHandlerMgr().add(new SEARCHBUSYFLATS());
		this.getCmdHandlerMgr().add(new SEARCHFLAT());
		this.getCmdHandlerMgr().add(new SEARCHFLATFORUSER());
		this.getCmdHandlerMgr().add(new GETDOORFLAT());
		this.getCmdHandlerMgr().add(new GET_FAVORITE_ROOMS());
		this.getCmdHandlerMgr().add(new ADD_FAVORITE_ROOM());
		this.getCmdHandlerMgr().add(new DEL_FAVORITE_ROOM());
		
		// Room-O-Matic
		this.getCmdHandlerMgr().add(new CREATEFLAT());
		this.getCmdHandlerMgr().add(new GETFLATINFO());
		this.getCmdHandlerMgr().add(new SETFLATINFO());
		this.getCmdHandlerMgr().add(new UPDATEFLAT());
		
		// Catalogue
		this.getCmdHandlerMgr().add(new GETORDERINFO());
		this.getCmdHandlerMgr().add(new PURCHASE());
		
		// CFH
		this.getCmdHandlerMgr().add(new CRYFORHELP());
	}

	public void stop(String reason)
	{
		// Destroy network connections
		try
		{
			this.getConnection().disconnect();
			if (this.getSpaceSession() != null)
			{
				this.getSpaceSession().exit(null);
			}
		}
		catch (Exception ex)
		{
			getServer().getLogger().error(this, "error stopping network connections", ex);
		}
		
		// Perform cleanup logic
		try
		{
			if (m_userObject != null)
			{
				
			}
		}
		catch (Exception ex)
		{
			getServer().getLogger().error(this, "error performing session cleanup operations", ex);
		}
	}
	
	public User getUserObject()
	{
		return m_userObject;
	}
	
	public Messenger getMessenger()
	{
		return m_messenger;
	}
	
	public Messenger createMessenger()
	{
		m_messenger = new Messenger(this);
		return m_messenger;
	}
}
