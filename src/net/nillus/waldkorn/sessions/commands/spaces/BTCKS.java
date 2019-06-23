package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.Waldkorn;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterSession;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.users.User;

public class BTCKS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Some magic numbers
		final short buyPrice = 2;
		final short ticketAmount = 5;
		
		// Can this user purchase?
		if(m_session.getUserObject().credits < buyPrice)
		{
			m_session.systemMsg("Sorry, but you do not have enough Credits to purchase this.");
			return;
		}
		
		// For who are the tickets bought?
		String receiverName = msg.nextArgument('/');
		boolean forSelf = (receiverName.equalsIgnoreCase(m_session.getUserObject().name));
		
		// Determine receiver
		User rcvr = null;
		if(forSelf)
		{
			rcvr = m_session.getUserObject();
		}
		else
		{
			rcvr = Waldkorn.getServer().getUserRegister().getUserInfo(receiverName);
			if(rcvr == null)
			{
				m_session.systemMsg("Sorry, but the user '" + receiverName + "' does not exist!");
				return;
			}
		}
		
		// Refresh buyer
		m_session.getUserObject().credits -= buyPrice;
		m_session.sendCredits();
		
		// Give tickets to receiver
		rcvr.gameTickets += ticketAmount;
		
		// Update buyer
		m_session.getUserObject().updateValuables(Waldkorn.getServer().getDatabase());
		
		// Update receiver
		if(forSelf)
		{
			m_session.sendGameTickets();
		}
		else
		{
			// Update data of other user
			rcvr.updateValuables(Waldkorn.getServer().getDatabase());
			
			// Notify other user if online
			MasterSession session2 = Waldkorn.getServer().getSessionMgr().getMasterSessionOfUser(rcvr.ID);

			if(session2 != null)
			{
				session2.sendGameTickets();
				session2.systemMsg(m_session.getUserObject().name + " has bought " + ticketAmount + " game tickets for you!");
			}
		}
	}
}
