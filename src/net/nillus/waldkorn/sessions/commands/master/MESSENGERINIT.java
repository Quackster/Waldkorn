package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.messenger.Messenger;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGERINIT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{		
		// Create messenger in session
		Messenger messenger = m_session.getMasterSession().createMessenger();

		// Register handlers for interaction with the messenger
		m_session.getCmdHandlerMgr().add(new MESSENGER_SENDUPDATE());
		m_session.getCmdHandlerMgr().add(new MESSENGER_ASSIGNPERSMSG());
		m_session.getCmdHandlerMgr().add(new MESSENGER_REQUESTBUDDY());
		m_session.getCmdHandlerMgr().add(new MESSENGER_REMOVEBUDDY());
		m_session.getCmdHandlerMgr().add(new MESSENGER_ACCEPTBUDDY());
		m_session.getCmdHandlerMgr().add(new MESSENGER_DECLINEBUDDY());
		m_session.getCmdHandlerMgr().add(new MESSENGER_SENDMSG());
		m_session.getCmdHandlerMgr().add(new MESSENGER_MARKREAD());
		
		// Load & send buddy list
		messenger.loadBuddies();
		messenger.sendBuddyList();
		
		// Load buddy requests
		messenger.loadBuddyRequests();
		
		// Load unread messages
		messenger.sendUnreadMessages();
		
		// Send SMS account (lol)
		m_response.set("MESSENGERSMSACCOUNT");
		m_response.appendNewArgument("noaccount");
		sendResponse();
		
		// Send messenger motto
		m_response.set("MYPERSISTENTMSG");
		m_response.appendNewArgument(m_session.getUserObject().messengerMotto);
		sendResponse();
		
		// Messenger is ready!
		m_session.getCmdHandlerMgr().remove("MESSENGERINIT");
		m_response.set("MESSENGERREADY");
		sendResponse();
	}
}
