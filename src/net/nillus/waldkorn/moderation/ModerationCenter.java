package net.nillus.waldkorn.moderation;

import java.util.Date;
import java.util.Vector;

import net.nillus.waldkorn.Server;
import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.MasterSession;

/**
 * ModerationCenter is where authorized Users perform their moderation tasks such as banning and kicking Users and replying Call for Helps.
 * 
 * @author Nillus
 */
public class ModerationCenter extends ServerComponent
{
	private static int m_callCounter;
	private static Vector<CallForHelp> m_pendingCalls;
	
	public ModerationCenter(Server server)
	{
		super(server);
		m_callCounter = 0;
		m_pendingCalls = new Vector<CallForHelp>();
	}
	
	public ModerationBan getBan(int ID)
	{
		ModerationBan ban = new ModerationBan();
		ban.ID = ID;
		
		// Not found / expired
		return null;
	}
	
	public ModerationBan getIpBan(String ip)
	{
		ModerationBan ban = new ModerationBan();
		ban.ip = ip;
		
		// Not found / expired
		return null;
	}
	
	public ModerationBan getUserBan(int userID)
	{
		ModerationBan ban = new ModerationBan();
		ban.userID = userID;
		
		// Not found / expired
		return null;
	}
	
	private boolean banIsValid(ModerationBan ban)
	{
		// Ban expired?
		if (new Date(System.currentTimeMillis()).after(ban.expiresAt))
		{
			// Try to delete ban from the system
			if (this.deleteBan(ban))
			{
				m_server.getLogger().info("ModerationCenter", "ban #" + ban.ID + " expired. [user ID: " + ban.userID + ", IP address: " + ban.ip + "]");
				
				// Ban expired aka not valid anymore!
				return false;
			}
		}
		
		// Ban is still valid!
		return true;
	}
	
	public boolean deleteBan(ModerationBan ban)
	{
		return false;
	}
	
	/*
	public ModerationBan setUserBan(int userID, boolean banIP, int hours, String reason, int issuerID)
	{
		// Get users last access entry
		UserAccessEntry lastAccessEntry = null;
		if (lastAccessEntry != null)
		{
			// Delete old user ban (if exists)
			ModerationBan ban = this.getUserBan(userID);
			if (ban != null)
			{
				this.deleteBan(ban);
			}
			
			// Delete old ip ban (if exists)
			if (banIP)
			{
				ban = this.getIpBan(lastAccessEntry.ip);
				if (ban != null)
				{
					this.deleteBan(ban);
				}
			}
			
			// Create the new ban
			ban = new ModerationBan();
			ban.userID = userID;
			ban.ip = (banIP) ? lastAccessEntry.ip : null;
			ban.appliedBy = issuerID;
			ban.reason = reason;
			
			// Work out expiration etc
			Calendar calendar = Calendar.getInstance();
			ban.appliedAt = calendar.getTime();
			if (hours > 0)
			{
				calendar.add(Calendar.HOUR, hours);
			}
			else
			{
				calendar.add(Calendar.YEAR, 10);
			}
			ban.expiresAt = calendar.getTime();
			
			// Insert in in the DatabaseEndpoint
			if (Environment.getDatabase().insert(ban))
			{
				if (ban.ip == null)
				{
					// Disconnect and notify the user
					MasterSession session = HabboHotel.getMasterSessions().getSessionOfUser(ban.userID);
					if (client != null)
					{
						client.sendBan(ban);
						client.stop("user is banned");
					}
				}
				else
				{
					// Disconnect and notify the users on this ip
					Vector<MasterSession> clients = HabboHotel.getMasterSessions().getSessionsWithIpAddress(ban.ip);
					for (MasterSession session : clients)
					{
						if (client != null)
						{
							client.sendBan(ban);
							client.stop("user IP is banned");
						}
					}
				}
				return ban;
			}
		}
		
		// Ban failed!
		return null;
	}
	*/
	
	public static CallForHelp createCallForHelp()
	{
		return new CallForHelp(++m_callCounter);
	}
	
	public static void submitCallForHelp(CallForHelp call)
	{
		// Add to pending calls!
		synchronized (m_pendingCalls)
		{
			m_pendingCalls.add(call);
		}
		
		// Broadcast to helpers
		ServerMessage notify = new ServerMessage("CRYFORHELP");
		notify.appendObject(call);
		broadcastToHelpers(notify);
	}
	
	public boolean pickCallForHelp(int callID, String picker)
	{
		CallForHelp call = this.getPendingCall(callID);
		if (call != null)
		{
			// Answered
			synchronized (m_pendingCalls)
			{
				m_pendingCalls.remove(call);
			}
			
			// Notify moderators call is picked
			ServerMessage notify = new ServerMessage("PICKED_CRY");
			notify.appendNewArgument(picker);
			notify.appendNewArgument(ModerationCenter.craftChatlogUrl(callID));
			this.broadcastToHelpers(notify);
			
			// Picked
			return true;
		}
		
		// Already picked / does not exist
		return false;
	}
	
	public static void broadcastToHelpers(ServerMessage msg)
	{
		byte minimumRole = 2;//HabboHotel.getAccessControl().getMinimumRoleForUserRight("can_answer_cfh");
		Vector<MasterSession> receivers = new Vector<MasterSession>();//HabboHotel.getMasterSessions().getSessionsWithUserRole(minimumRole);
		for (MasterSession session : receivers)
		{
			session.send(msg);
		}
	}
	
	public CallForHelp getPendingCall(int callID)
	{
		synchronized (m_pendingCalls)
		{
			for (CallForHelp call : m_pendingCalls)
			{
				if (call.ID == callID)
				{
					return call;
				}
			}
		}
		
		return null;
	}
	
	public int clearPendingCalls()
	{
		synchronized (m_pendingCalls)
		{
			int amount = m_pendingCalls.size();
			m_pendingCalls.clear();
			return amount;
		}
	}
	
	public Vector<CallForHelp> getPendingCalls()
	{
		return m_pendingCalls;
	}
	
	public static String craftChatlogUrl(int callID)
	{
		return "/chatlog.php?id=" + callID;
	}
	
	public static int parseCallID(String chatlogUrl)
	{
		try
		{
			return Integer.parseInt(chatlogUrl.substring("/chatlog.php?id=".length()));
		}
		catch (Exception ex)
		{
			return -1;
		}
	}
}
