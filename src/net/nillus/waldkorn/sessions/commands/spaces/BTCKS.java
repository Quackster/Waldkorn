package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.clients.MasterSession;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.MasterClient;
import net.nillus.waldkorn.users.User;

public class BTCKS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Some magic numbers
		final short buyPrice = 2;
		final short ticketAmount = 5;
		
		// Can this user purchase?
		if(client.getUserObject().credits < buyPrice)
		{
			client.systemMsg("Sorry, but you do not have enough Credits to purchase this.");
			return;
		}
		
		// For who are the tickets bought?
		String receiverName = msg.nextArgument('/');
		boolean forSelf = (receiverName.equalsIgnoreCase(client.getUserObject().name));
		
		// Determine receiver
		User rcvr = null;
		if(forSelf)
		{
			rcvr = client.getUserObject();
		}
		else
		{
			rcvr = HabboHotel.getUserRegister().getUserInfo(receiverName, true);
			if(rcvr == null)
			{
				client.systemMsg("Sorry, but the user '" + receiverName + "' does not exist!");
				return;
			}
		}
		
		// Refresh buyer
		client.getUserObject().credits -= buyPrice;
		client.sendCredits();
		
		// Give tickets to receiver
		rcvr.gameTickets += ticketAmount;
		
		// Update buyer
		HabboHotel.getUserRegister().updateUser(client.getUserObject());
		
		// Update receiver
		if(forSelf)
		{
			client.sendGameTickets();
		}
		else
		{
			// Update data of other user
			HabboHotel.getUserRegister().updateUser(rcvr);
			
			// Notify other user if online
			MasterSession session2 = HabboHotel.getMasterSessions().getSessionOfUser(rcvr.ID);
			if(client2 != null)
			{
				client2.sendGameTickets();
				client2.systemMsg(client.getUserObject().name + " has bought " + ticketAmount + " game tickets for you!");
			}
		}
	}
}
