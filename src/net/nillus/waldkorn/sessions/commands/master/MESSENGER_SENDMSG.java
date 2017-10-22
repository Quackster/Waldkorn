package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;

public class MESSENGER_SENDMSG extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Parse the message text
		String[] data = msg.getBody().split("\r", 2);
		data[1] = data[1].replace((char)13, (char)10);
		
		// Parse the receiver IDs
		String[] receiverString = data[0].split(" ");
		int[] receiverIDs = new int[receiverString.length];
		for(int i = 0; i < receiverIDs.length; i++)
		{
			try
			{
				receiverIDs[i] = Integer.parseInt(receiverString[i]);
			}
			catch(NumberFormatException ex)
			{
				return;
			}
		}
		
		// Process it
		m_session.getMasterSession().getMessenger().sendMessage(receiverIDs, data[1]);
	}
}
